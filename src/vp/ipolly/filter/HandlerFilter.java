package vp.ipolly.filter;

import vp.ipolly.service.Session;

/**
 * filter链尾，负责调用handler
 * @author vpsong
 *
 */
public class HandlerFilter extends FilterAdaptor {

	@Override
	public void read(Session session, Object message) {
		session.getHandler().received(session, message);
	}

	@Override
	public void send(Session session, Object message) {
		session.getHandler().sent(session, message);
	}
}
