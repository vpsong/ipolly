package vp.ipolly.client;

import java.net.InetSocketAddress;

import vp.ipolly.handler.DefaultHandlerImpl;
import vp.ipolly.handler.HandlerAdapter;
import vp.ipolly.service.Connector;
import vp.ipolly.service.Session;
import vp.ipolly.service.tcp.TcpConnector;

public class HttpClient {

	public static void main(String[] args) {
		Session session = new TcpConnector(new InetSocketAddress("localhost", 1111),
				new DefaultHandlerImpl()).connect();
		session.write("new Message");
	}

}
