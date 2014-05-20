package vp.ipolly.service.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Logger;

import vp.ipolly.handler.Handler;
import vp.ipolly.service.Connector;
import vp.ipolly.service.Processor;

/**
 * 
 * @author vpsong
 * 
 */
public class TcpConnector implements Connector {

	private Logger logger = Logger
			.getLogger(TcpConnector.class.getSimpleName());

	private Selector selector;
	private final InetSocketAddress serverAddress;
	private SocketChannel socketChannel;
	private Handler handler;
	private boolean running;
	private Processor[] processorArray;
	private static final int processorSize = 3;
	private int processCount;
	private static final int DEFAULT_BYTEBUFFER_SIZE = 2048;
	private Worker worker;

	public TcpConnector(InetSocketAddress address, Handler handler) {
		this.serverAddress = address;
		this.handler = handler;
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void startup() {
		if (!running) {
			try {
				selector = Selector.open();
				socketChannel.register(selector, SelectionKey.OP_CONNECT);
				socketChannel.connect(serverAddress);
				processorArray = new TcpProcessor[processorSize];
				for (int i = 0; i < processorSize; ++i) {
					processorArray[i] = new TcpProcessor();
					processorArray[i].startup();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			running = true;
			worker = new Worker();
			new Thread(worker).start();
			logger.info("client has started up");
		}
	}

	public synchronized void shutdown() {
		if (!running) {
			return;
		}
		running = false;
		if (socketChannel != null) {
			try {
				socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
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
				if (socketChannel.isConnectionPending() && socketChannel.finishConnect()) {
					nextProcessor().addNew(new TcpSession(socketChannel, handler));
					handler.connected();
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