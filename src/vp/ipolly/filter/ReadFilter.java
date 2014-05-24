package vp.ipolly.filter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import vp.ipolly.service.Session;

public class ReadFilter extends FilterAdaptor {

	private static final int DEFAULT_BYTEBUFFER_SIZE = 2048;

	@Override
	public void read(Session session, Object message) {
		SocketChannel socketChannel = session.getSocketChannel();
		ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BYTEBUFFER_SIZE);
		int bytes = 0;
		int ret = 0;
		try {
			while ((ret = socketChannel.read(buffer)) > 0) {
				bytes += ret;
			}
			if (bytes <= 0) {
				return;
			}
			session.increaseReadBytes(bytes);
			message = new String(buffer.array());
			nextFilter().read(session, message);
			session.getHandler().received(session, message);
		} catch (IOException e) {
			session.close();
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
