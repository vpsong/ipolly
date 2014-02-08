package vp.ipolly.handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 
 * @author vp.song
 * 
 */
public class AcceptHandler implements Runnable {

	private final Selector selector;
	private final ServerSocketChannel serverSocketChannel;

	public AcceptHandler(Selector selector,
			ServerSocketChannel serverSocketChannel) {
		this.selector = selector;
		this.serverSocketChannel = serverSocketChannel;
	}

	@Override
	public void run() {
		try {
			SocketChannel channel = serverSocketChannel.accept();
			if (channel != null) {
				new ReadHandler(selector, channel);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}