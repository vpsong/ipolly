package vp.ipolly.handler;

import vp.ipolly.service.Session;

/**
 * 默认Handler实现
 * @author vp.song
 * 
 */
public class DefaultHandlerImpl extends HandlerAdapter {

	@Override
	public void accepted(Session session) {
		System.out.println("accepted a client");
	}

	@Override
	public void connected(Session session) {
		System.out.println("connected to server");
	}

	@Override
	public void received(Session session, Object message) {
		System.out.println("receive");
	}

	@Override
	public void sent(Session session, Object message) {
		System.out.println("sent");
	}

}
