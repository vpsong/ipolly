package vp.ipolly.connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

import vp.ipolly.handler.AcceptHandler;

/**
 * 
 * @author vp.song
 * 
 */
public class HttpConnector implements Runnable {

	final Selector selector;

	final ServerSocketChannel serverSocketChannel;

	public HttpConnector(int port) throws IOException {
		selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);
		SelectionKey key = serverSocketChannel.register(selector,
				SelectionKey.OP_ACCEPT);
		key.attach(new AcceptHandler(selector, serverSocketChannel));
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				selector.select();
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				for (SelectionKey selectionKey : selectionKeys) {
					dispatch(selectionKey);
				}
				selectionKeys.clear();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.interrupted();
			}
		}

	}

	private void dispatch(SelectionKey selectionKey) {
		Runnable run = (Runnable) selectionKey.attachment();
		if (run != null) {
			run.run();
		}
	}

}
