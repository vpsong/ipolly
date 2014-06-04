package vp.ipolly.handler;

import vp.ipolly.service.Session;

public class HandlerAdapter implements Handler {
	
	@Override
	public void accepted(Session session) {
	}
	
	@Override
	public void connected(Session session) {
	}

	@Override
	public void received(Session session, Object message){
	}

	@Override
	public void sent(Session session, Object message){
	}

}
