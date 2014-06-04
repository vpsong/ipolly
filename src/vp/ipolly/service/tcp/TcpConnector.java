package vp.ipolly.service.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import vp.ipolly.handler.Handler;
import vp.ipolly.service.Connector;
import vp.ipolly.service.Processor;
import vp.ipolly.service.Session;
import vp.ipolly.service.common.ExecutorThreadPool;

/**
 * 
 * @author vpsong
 * 
 */
public class TcpConnector implements Connector {

	private Logger logger = Logger
			.getLogger(TcpConnector.class.getSimpleName());

	private Selector selector;
	private List<SocketChannel> channelList = new ArrayList<SocketChannel>(2);
	private SocketChannel socketChannel;
	private Handler handler;
	private volatile boolean running;
	private Processor[] processorArray;
	/**
	 * 默认起3个processor
	 */
	private static final int processorSize = 3;
	/**
	 * accept了多少个
	 */
	private volatile int processCount;
	private Worker worker;

	// private Session session;

	public TcpConnector(Handler handler) {
		this.handler = handler;
	}

	public synchronized Session connect(InetSocketAddress serverAddress) {
		Session session = null;
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.connect(serverAddress);
			channelList.add(socketChannel);
			session = new TcpSession(socketChannel, handler);
			socketChannel.register(selector, SelectionKey.OP_CONNECT, session);
			if (!running) {
				running = true;
				processorArray = new TcpProcessor[processorSize];
				for (int i = 0; i < processorSize; ++i) {
					processorArray[i] = new TcpProcessor();
					processorArray[i].startup();
				}
				worker = new Worker();
				ExecutorThreadPool.getExecutor().execute(worker);
				logger.info("client has started up");
			}
			nextProcessor().addNew(session);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return session;
	}

	public synchronized void disconnect() {
		running = false;
		for (SocketChannel socketChannel : channelList) {
			if (socketChannel != null) {
				try {
					socketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Processor nextProcessor() {
		++processCount;
		return processorArray[processCount % processorSize];
	}

	private void handle(SelectionKey selectionKey) {
		if (selectionKey.isConnectable()) {
			try {
				if (socketChannel.isConnectionPending()
						&& socketChannel.finishConnect()) {
					Session session = (Session) selectionKey.attachment();
					handler.connected(session);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doSelect() {
		try {
			selector.select();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Set<SelectionKey> selectionKeys = selector.selectedKeys();
		for (SelectionKey selectionKey : selectionKeys) {
			handle(selectionKey);
		}
		selectionKeys.clear();
	}

	private class Worker implements Runnable {

		@Override
		public void run() {
			Thread.currentThread().setName("TcpConnector_Worker");
			while (running) {
				doSelect();
			}
		}

	}

}