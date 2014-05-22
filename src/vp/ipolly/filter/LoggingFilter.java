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
	public int read(Session session, Object message) {
		int bytes = nextFilter().read(session, message);
		if (bytes > 0) {
			logger.info("receive:" + String.valueOf(message));
		}
		return bytes;
	}

	@Override
	public int send(Session session, Object message) {
		logger.info("send:" + String.valueOf(message));
		return nextFilter().send(session, message);
	}

}
