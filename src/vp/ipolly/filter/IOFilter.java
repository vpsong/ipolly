package vp.ipolly.filter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import vp.ipolly.service.Session;
import vp.ipolly.service.common.Data;

/**
 * fiter链头，负责读写IO
 * @author vpsong
 *
 */
public class IOFilter extends FilterAdaptor {

	private static final int DEFAULT_BYTEBUFFER_SIZE = 2048;

	@Override
	public void read(Session session, Object msg) {
		ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BYTEBUFFER_SIZE);
		int bytes = 0;
		int ret = 0;
		try {
			while ((ret = session.read0(buffer)) > 0) {
				bytes += ret;
			}
			if (bytes <= 0) {
				return;
			}
			buffer.flip();
			session.increaseReadBytes(bytes);
			byte[] message = new byte[buffer.limit()];
			buffer.get(message);
			Data data = new Data();
			data.setData(message);
			nextFilter().read(session, data);
		} catch (IOException e) {
			session.scheduledClose();
			e.printStackTrace();
		} catch (Exception e) {
			session.scheduledClose();
			e.printStackTrace();
		}
	}
	
	@Override
	public void send(Session session, Object message) {
		Data data = new Data();
		data.setData(message);
		nextFilter().send(session, data);
		ByteBuffer buffer = ByteBuffer.wrap((byte[]) data.getData());
		try {
			session.write0(buffer);
			session.increaseWrittenBytes(buffer.limit());
		} catch (IOException e) {
			session.scheduledClose();
			e.printStackTrace();
		}
	}
}
