package vp.ipolly.filter;

import vp.ipolly.service.Session;

public class HandlerFilter extends FilterAdaptor {

	@Override
	public void read(Session session, Object message) {
		session.getHandler().received(session, message);
	}

	@Override
	public void send(Session session, Object message) {
		session.getHandler().writed(session, message);
	}
}
