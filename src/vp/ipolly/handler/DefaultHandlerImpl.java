package vp.ipolly.handler;

import vp.ipolly.service.Session;

/**
 * 
 * @author vp.song
 * 
 */
public class DefaultHandlerImpl extends HandlerAdapter {

	@Override
	public void accepted() {
		System.out.println("accepted a client");
	}

	@Override
	public void connected() {
		System.out.println("connected to server");
	}

	@Override
	public void received(Session session, Object message) {
	}

	@Override
	public void writed(Session session, Object message) {
	}

}
