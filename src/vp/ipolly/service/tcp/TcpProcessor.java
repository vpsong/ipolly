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

import vp.ipolly.filter.FilterChain;
import vp.ipolly.service.Processor;
import vp.ipolly.service.Session;
import vp.ipolly.service.common.ExecutorThreadPool;

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
	private static final int SELECT_TIMEOUT = 1000;
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
			ExecutorThreadPool.getExecutor().execute(worker);
			reader = new Reader();
			ExecutorThreadPool.getExecutor().execute(reader);
			writer = new Writer();
			ExecutorThreadPool.getExecutor().execute(writer);
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
		session.getFilterChain().read(session, null);
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
		Queue<Object> writeQueue = session.getWriteQueue();
		while (!writeQueue.isEmpty()) {
			Object msg = writeQueue.poll();
			session.getFilterChain().send(session, msg);
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
