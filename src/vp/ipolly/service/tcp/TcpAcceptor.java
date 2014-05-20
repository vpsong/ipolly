package vp.ipolly.service.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Logger;

import vp.ipolly.handler.Handler;
import vp.ipolly.service.Acceptor;
import vp.ipolly.service.Processor;

/**
 * 
 * @author vpsong
 * 
 */
public class TcpAcceptor implements Acceptor {

	private Logger logger = Logger.getLogger(TcpAcceptor.class.getSimpleName());

	private final int port;
	private Handler handler;
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private boolean running;
	private Processor[] processorArray;
	private static final int processorSize = 3;
	private int processCount;
	private static final int DEFAULT_BYTEBUFFER_SIZE = 2048;
	private Worker worker;

	public TcpAcceptor(int port, Handler handler) {
		this.port = port;
		this.handler = handler;
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void startup() {
		if (!running) {
			try {
				selector = Selector.open();
				serverSocketChannel.configureBlocking(false);
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
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
			logger.info("server has started up");
		}
	}

	public synchronized void shutdown() {
		if (!running) {
			return;
		}
		running = false;
		if (serverSocketChannel != null) {
			try {
				serverSocketChannel.close();
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
		SocketChannel socketChannel = null;
		if (selectionKey.isAcceptable()) {
			try {
				socketChannel = serverSocketChannel.accept();
				nextProcessor().addNew(new TcpSession(socketChannel, handler));
				handler.accepted();
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
