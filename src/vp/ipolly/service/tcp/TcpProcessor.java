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
import vp.ipolly.service.SessionState;
import vp.ipolly.service.common.Data;
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
	
	/**
	 * 运行标志
	 */
	private boolean running;
	
	/**
	 * processor实例名
	 */
	private String name;
	private static final String NAME_PREFIX = "TcpProcessor_";
	private Queue<Session> newSessionQueue = new ConcurrentLinkedQueue<Session>();
	private Queue<Session> removeSessionQueue = new ConcurrentLinkedQueue<Session>();
	private Queue<Session> regWriteSessionQueue = new ConcurrentLinkedQueue<Session>();
	private BlockingQueue<Session> writeSessionQueue = new LinkedBlockingQueue<Session>();
	private BlockingQueue<Session> readSessionQueue = new LinkedBlockingQueue<Session>();
	
	/**
	 * 最近一次空闲检查时间
	 */
	private long lastProcessIdleTime;
	private Worker worker;
	private Reader reader;
	private Writer writer;
	
	/**
	 * 实例数
	 */
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

	public void scheduledRemove(Session session) {
		removeSessionQueue.add(session);
	}

	private void processNew() {
		while (!newSessionQueue.isEmpty()) {
			Session session = newSessionQueue.poll();
			session.init();
		}
	}

	private void processRemove() {
		while (!removeSessionQueue.isEmpty()) {
			Session session = removeSessionQueue.poll();
			session.close();
		}
	}

	private void processIdle() {
		long nowTime = System.currentTimeMillis();
		if (nowTime - lastProcessIdleTime > PROCESS_IDLE_INTERVAL) {
			lastProcessIdleTime = nowTime;
			Set<SelectionKey> keySet = selector.keys();
			if (keySet == null) {
				return;
			}
			for (SelectionKey key : keySet) {
				Session session = (Session) key.attachment();
				if ((nowTime - session.getLastReadTime() > IDLE_TIMEOUT)
						&& (nowTime - session.getLastWriteTime() > IDLE_TIMEOUT)) {
					session.getFilterChain().idle(session);
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
		session.getFilterChain().read(session);
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
				processIdle();
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

	public Selector getSelector() {
		return selector;
	}

}
