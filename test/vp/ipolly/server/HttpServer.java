package vp.ipolly.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vp.ipolly.handler.DefaultHandlerImpl;
import vp.ipolly.handler.HandlerAdapter;
import vp.ipolly.service.Acceptor;
import vp.ipolly.service.tcp.TcpAcceptor;

public class HttpServer {

	public static ExecutorService es = Executors.newCachedThreadPool();

	public static void main(String[] args) {
		new TcpAcceptor(1111, new DefaultHandlerImpl()).startup();
	}
}
