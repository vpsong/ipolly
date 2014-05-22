package vp.ipolly.filter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import vp.ipolly.service.Session;

/**
 * 
 * @author vpsong
 * 
 */
public class IOFilter extends FilterAdaptor {

	private static final int DEFAULT_BYTEBUFFER_SIZE = 2048;

	@Override
	public int read(Session session, Object message) {
		int bytes = 0;
		SocketChannel socketChannel = session.getSocketChannel();
		ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BYTEBUFFER_SIZE);
		try {
			while (socketChannel.read(buffer) > 0) {
				message = new String(buffer.array());
				bytes += buffer.limit();
				session.getHandler().received(message);
			}
		} catch (IOException e) {
			session.close();
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}

	@Override
	public int send(Session session, Object message) {
		ByteBuffer buffer = ByteBuffer.wrap(String.valueOf(message).getBytes());
		try {
			session.getSocketChannel().write(buffer);
			session.getHandler().writed(new String(buffer.array()));
		} catch (IOException e) {
			session.close();
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.limit();
	}

}
