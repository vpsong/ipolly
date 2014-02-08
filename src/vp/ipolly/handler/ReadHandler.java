package vp.ipolly.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 
 * @author vp.song
 * 
 */
public class ReadHandler implements Runnable {


	private final SocketChannel socketChannel;

	private final SelectionKey seletionKey;
	
	private ByteBuffer buffer = ByteBuffer.allocate(10240);

	public ReadHandler(Selector selector, SocketChannel channel)
			throws IOException {
		this.socketChannel = channel;
		socketChannel.configureBlocking(false);
		this.seletionKey = socketChannel.register(selector, 0);
		seletionKey.attach(this);
		seletionKey.interestOps(SelectionKey.OP_READ);
		selector.wakeup();
	}

	@Override
	public void run() {
		try {
			socketChannel.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
