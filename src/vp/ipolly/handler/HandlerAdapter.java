package vp.ipolly.handler;

import vp.ipolly.service.Session;

public class HandlerAdapter implements Handler {
	
	@Override
	public void accepted() {
	}
	
	@Override
	public void connected() {
	}

	@Override
	public void received(Session session, Object message){
	}

	@Override
	public void writed(Session session, Object message){
	}

}
