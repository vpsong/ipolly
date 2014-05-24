package vp.ipolly.filter;

import java.io.IOException;
import java.nio.ByteBuffer;
import vp.ipolly.service.Session;

public class WriteFilter extends FilterAdaptor {

	@Override
	public void send(Session session, Object message) {
		ByteBuffer buffer = ByteBuffer.wrap(String.valueOf(message).getBytes());
		try {
			session.getSocketChannel().write(buffer);
			session.getHandler().writed(session, new String(buffer.array()));
			session.increaseWrittenBytes(buffer.limit());
		} catch (IOException e) {
			session.close();
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
