package vp.ipolly.handler;

import vp.ipolly.service.Session;

/**
 * 
 * @author vp.song
 *
 */
public interface Handler {

	void accepted();
	void connected();
	void received(Session session, Object message) throws Exception;
	void writed(Session session, Object message) throws Exception;
	
}
