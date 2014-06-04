package vp.ipolly.filter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import vp.ipolly.service.Session;

/**
 * 黑名单过滤
 * @author vpsong
 *
 */
public class BlackListFilter extends FilterAdaptor {
	
	private Logger logger = Logger.getLogger(BlackListFilter.class.getSimpleName());
	
	private Set<InetSocketAddress> blackList = new HashSet<InetSocketAddress>();
	
	/**
	 * 是否在黑名单里
	 * @param session
	 * @return
	 */
	private boolean inBlackList(Session session) {
		SocketAddress remote = null;
		try {
			remote = session.getRemoteAddress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(remote != null && !blackList.contains(remote)) {
			return false;
		}
		session.scheduledClose();
		logger.info("block socket " + remote + " in blackList...");
		return true;
	}
	
	@Override
	public void read(Session session, Object message) {
		if(!inBlackList(session)) {
			nextFilter().read(session, message);
		}
	}

	@Override
	public void send(Session session, Object message) {
		if(!inBlackList(session)) {
			nextFilter().send(session, message);
		}
	}

}
