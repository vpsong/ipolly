package vp.ipolly.client;

import java.net.InetSocketAddress;

import vp.ipolly.handler.DefaultHandlerImpl;
import vp.ipolly.protocol.impl.DefaultMessage;
import vp.ipolly.service.Connector;
import vp.ipolly.service.Session;
import vp.ipolly.service.tcp.TcpConnector;

public class HttpClient {

	public static void main(String[] args) {
		Connector connector = new TcpConnector(new DefaultHandlerImpl());
		Session session = connector.connect(new InetSocketAddress("localhost",
				1111));
		DefaultMessage msg = new DefaultMessage();
		msg.setData("我爱你");
		msg.setLength(msg.getData().getBytes().length);
		session.write(msg);
	}

}
