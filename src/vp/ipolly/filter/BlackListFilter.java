package vp.ipolly.filter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import vp.ipolly.service.Session;

public class BlackListFilter extends FilterAdaptor {
	
	private Logger logger = Logger.getLogger(BlackListFilter.class.getSimpleName());
	
	private Set<InetSocketAddress> blackList = new HashSet<InetSocketAddress>();
	
	private boolean inBlackList(Session session) {
		SocketAddress remote = null;
		try {
			remote = session.getSocketChannel().getRemoteAddress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(remote != null && !blackList.contains(remote)) {
			return false;
		}
		session.close();
		logger.info("block socket " + remote + " in blackList...");
		return true;
	}
	
	@Override
	public int read(Session session, Object message) {
		if(!inBlackList(session)) {
			return nextFilter().read(session, message);
		}
		return -1;
	}

	@Override
	public int send(Session session, Object message) {
		if(!inBlackList(session)) {
			return nextFilter().send(session, message);
		}
		return -1;
	}

}
