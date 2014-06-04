package vp.ipolly.service.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import vp.ipolly.handler.Handler;
import vp.ipolly.service.Acceptor;
import vp.ipolly.service.Processor;
import vp.ipolly.service.Session;
import vp.ipolly.service.common.ExecutorThreadPool;

/**
 * 
 * @author vpsong
 * 
 */
public class TcpAcceptor implements Acceptor {

	private Logger logger = Logger.getLogger(TcpAcceptor.class.getSimpleName());

	private List<ServerSocketChannel> channelList = new ArrayList<ServerSocketChannel>(
			2);
	private Handler handler;
	private Selector selector;
	/**
	 * 运行标志
	 */
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

	public TcpAcceptor(Handler handler) {
		this.handler = handler;
	}

	public void bind(int port) {
		try {
			selector = Selector.open();
			InetSocketAddress localAddress = new InetSocketAddress(port);
			ServerSocketChannel serverSocketChannel = ServerSocketChannel
					.open();
			serverSocketChannel.socket().bind(localAddress);
			serverSocketChannel.configureBlocking(false);
			channelList.add(serverSocketChannel);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			if (!running) {
				running = true;
				processorArray = new TcpProcessor[processorSize];
				for (int i = 0; i < processorSize; ++i) {
					processorArray[i] = new TcpProcessor();
					processorArray[i].startup();
				}
				worker = new Worker();
				ExecutorThreadPool.getExecutor().execute(worker);
				logger.info("server has started up");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void shutdown() {
		running = false;
		for (ServerSocketChannel serverSocketChannel : channelList) {
			if (serverSocketChannel != null) {
				try {
					serverSocketChannel.close();
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
		if (selectionKey.isAcceptable()) {
			try {
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey
						.channel();
				SocketChannel socketChannel = serverSocketChannel.accept();
				Session session = new TcpSession(socketChannel, handler);
				nextProcessor().addNew(session);
				handler.accepted(session);
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
			Thread.currentThread().setName("TcpAcceptor_Worker");
			while (running) {
				doSelect();
			}
		}

	}
}
