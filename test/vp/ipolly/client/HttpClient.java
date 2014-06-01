package vp.ipolly.client;

import java.net.InetSocketAddress;

import vp.ipolly.handler.DefaultHandlerImpl;
import vp.ipolly.protocol.impl.DefaultMessage;
import vp.ipolly.service.Session;
import vp.ipolly.service.tcp.TcpConnector;

public class HttpClient {

	public static void main(String[] args) {
		Session session = new TcpConnector(new InetSocketAddress("localhost", 1111),
				new DefaultHandlerImpl()).connect();
		DefaultMessage msg = new DefaultMessage();
		msg.setData("我爱你");
		msg.setLength(msg.getData().getBytes().length);
		session.write(msg);
	}

}
