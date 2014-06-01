package vp.ipolly.filter;

import vp.ipolly.service.Session;
import vp.ipolly.service.common.Data;


/**
 * 
 * @author vpsong
 *
 */
public class FilterChain {
	
	private static FilterChain chain = new FilterChain();
	private Filter headFilter;
	
	private FilterChain() {
		headFilter = new IOFilter();
		BlackListFilter blackListFilter = new BlackListFilter();
		headFilter.setNextFilter(blackListFilter);
		HeartbeatFilter heartbeatFilter = new HeartbeatFilter();
		blackListFilter.setNextFilter(heartbeatFilter);
		ProtocolFilter protocolFilter = new ProtocolFilter();
		heartbeatFilter.setNextFilter(protocolFilter);
		LoggingFilter loggingFilter = new LoggingFilter();
		protocolFilter.setNextFilter(loggingFilter);
		loggingFilter.setNextFilter(new HandlerFilter());
	}
	
	public void read(Session session) {
		headFilter.read(session, null);
	}
	
	public void send(Session session, Object message) {
		headFilter.send(session, message);
	}
	
	public void idle(Session session) {
		headFilter.idle(session);
	}
	
	public static FilterChain getChain() {
		return chain;
	}

	public Filter getHeadFilter() {
		return headFilter;
	}

	public void setHeadFilter(Filter headFilter) {
		this.headFilter = headFilter;
	}
	
}
