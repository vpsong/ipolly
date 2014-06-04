package vp.ipolly.handler;

import vp.ipolly.service.Session;

/**
 * 
 * @author vp.song
 * 
 */
public interface Handler {

	void accepted(Session session);

	void connected(Session session);

	void received(Session session, Object message);

	void sent(Session session, Object message);

}
