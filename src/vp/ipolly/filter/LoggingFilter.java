package vp.ipolly.filter;

import java.util.logging.Logger;

import vp.ipolly.service.Session;

/**
 * 
 * @author vpsong
 * 
 */
public class LoggingFilter extends FilterAdaptor {

	private Logger logger = Logger.getLogger(LoggingFilter.class
			.getSimpleName());

	@Override
	public void read(Session session, Object message) {
		logger.info("receive: " + String.valueOf(message));
		nextFilter().read(session, message);
	}

	@Override
	public void send(Session session, Object message) {
		logger.info("send: " + String.valueOf(message));
		nextFilter().send(session, message);
	}

}
