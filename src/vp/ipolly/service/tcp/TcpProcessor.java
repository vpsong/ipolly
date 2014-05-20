package vp.ipolly.service.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import vp.ipolly.service.Processor;
import vp.ipolly.service.Session;

/**
 * 
 * @author vpsong
 * 
 */
public class TcpProcessor implements Processor {

	private Logger logger = Logger
			.getLogger(TcpProcessor.class.getSimpleName());

	private Selector selector;
	private boolean running;
	private String name;
	private static final String NAME_PREFIX = "TcpProcessor_";
	private Queue<Session> newSessionQueue = new ConcurrentLinkedQueue<Session>();
	private Queue<Session> removeSessionQueue = new ConcurrentLinkedQueue<Session>();
	private Queue<Session> regWriteSessionQueue = new ConcurrentLinkedQueue<Session>();
	private BlockingQueue<Session> writeSessionQueue = new LinkedBlockingQueue<Session>();
	private BlockingQueue<Session> readSessionQueue = new LinkedBlockingQueue<Session>();
	private static final int DEFAULT_BYTEBUFFER_SIZE = 2048;
	private static final int SELECT_TIMEOUT = 3000;
	private Worker worker;
	private Reader reader;
	private Writer writer;
	private static int instance_count;

	public TcpProcessor() {
		this.name = NAME_PREFIX + instance_count;
		++instance_count;
	}

	public synchronized void startup() {
		if (!running) {
			try {
				selector = Selector.open();
			} catch (IOException e) {
				e.printStackTrace();
			}
			running = true;
			worker = new Worker();
			new Thread(worker).start();
			reader = new Reader();
			new Thread(reader).start();
			writer = new Writer();
			new Thread(writer).start();
			logger.info(name + " has started up");
		}
	}

	public synchronized void shutdown() {
		if (!running) {
			return;
		}
		running = false;
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addNew(Session session) {
		session.setProcessor(this);
		newSessionQueue.add(session);
	}

	public void remove(Session session) {
		removeSessionQueue.add(session);
	}

	private void processNew() {
		while (!newSessionQueue.isEmpty()) {
			Session session = newSessionQueue.poll();
			SocketChannel socketChannel = session.getSocketChannel();
			try {
				socketChannel.configureBlocking(false);
				session.setSelectionKey(socketChannel.register(selector,
						SelectionKey.OP_READ, session));
			} catch (IOException e) {
				e.printStackTrace();
			}
			session.write("I love you, 520");
		}
	}

	private void processRemove() {
		while (!removeSessionQueue.isEmpty()) {
			Session session = removeSessionQueue.poll();
			SelectionKey key = session.getSelectionKey();
			if (key != null) {
				key.cancel();
			}
			SocketChannel socketChannel = session.getSocketChannel();
			if (socketChannel != null) {
				try {
					socketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void doSelect() {
		try {
			selector.select(SELECT_TIMEOUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Set<SelectionKey> selectionKeys = selector.selectedKeys();
		for (SelectionKey selectionKey : selectionKeys) {
			handle(selectionKey);
		}
		selectionKeys.clear();
	}

	// private void read(Session session) {
	// SocketChannel socketChannel = session.getSocketChannel();
	// ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BYTEBUFFER_SIZE);
	// try {
	// socketChannel.read(buffer);
	// session.getHandler().received(new String(buffer.array()));
	// } catch (IOException e) {
	// remove(session);
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public void regWriteOps(Session session) {
		regWriteSessionQueue.add(session);
	}

	private void updateOps() {
		while (!regWriteSessionQueue.isEmpty()) {
			Session session = regWriteSessionQueue.poll();
			SelectionKey key = session.getSelectionKey();
			key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
		}
	}

	private void wakeup() {
		selector.wakeup();
	}

	// private void write(Session session) {
	// SelectionKey key = session.getSelectionKey();
	// key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
	//
	// SocketChannel ch = session.getSocketChannel();
	// Queue<Object> writeRequestQueue = session.getWriteQueue();
	//
	// key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
	// }

	private void processRead() {
		Session session = null;
		try {
			session = readSessionQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!session.isConncted()) {
			return;
		}
		SocketChannel socketChannel = session.getSocketChannel();
		ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BYTEBUFFER_SIZE);
		try {
			socketChannel.read(buffer);
			session.getHandler().received(new String(buffer.array()));
		} catch (IOException e) {
			remove(session);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processWrite() {
		Session session = null;
		try {
			session = writeSessionQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!session.isConncted()) {
			return;
		}
		SocketChannel socketChannel = session.getSocketChannel();
		Queue<Object> writeQueue = session.getWriteQueue();
		try {
			while (!writeQueue.isEmpty()) {
				Object msg = writeQueue.poll();
				ByteBuffer buffer = ByteBuffer.wrap(String.valueOf(msg).getBytes());
				socketChannel.write(buffer);
				session.getHandler().writed(new String(buffer.array()));
			}
		} catch (IOException e) {
			remove(session);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SelectionKey key = session.getSelectionKey();
		key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
	}

	private void read(Session session) {
		try {
			readSessionQueue.put(session);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void write(Session session) {
		try {
			writeSessionQueue.put(session);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void handle(SelectionKey selectionKey) {
		Session session = (Session) selectionKey.attachment();
		if (session.isReadable()) {
			read(session);
		} else if (session.isWritable()) {
			write(session);
		}
	}

	private class Worker implements Runnable {

		@Override
		public void run() {
			Thread.currentThread().setName(TcpProcessor.this.name + "_Worker");
			while (running) {
				processNew();
				updateOps();
				doSelect();
				processRemove();
			}
		}

	}

	private class Reader implements Runnable {

		@Override
		public void run() {
			Thread.currentThread().setName(TcpProcessor.this.name + "_Reader");
			while (running) {
				processRead();
			}
		}

	}

	private class Writer implements Runnable {

		@Override
		public void run() {
			Thread.currentThread().setName(TcpProcessor.this.name + "_Writer");
			while (running) {
				processWrite();
			}
		}

	}
}
