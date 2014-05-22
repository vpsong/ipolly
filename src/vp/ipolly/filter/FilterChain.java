package vp.ipolly.filter;

import vp.ipolly.service.Session;


/**
 * 
 * @author vpsong
 *
 */
public class FilterChain {
	
	private static FilterChain chain = new FilterChain();
	private Filter headFilter;
	
	private FilterChain() {
		headFilter = new LoggingFilter();
		BlackListFilter blackListFilter = new BlackListFilter();
		headFilter.setNextFilter(blackListFilter);
		blackListFilter.setNextFilter(new IOFilter());
	}
	
	public void read(Session session, Object message) {
		headFilter.read(session, message);
	}
	
	public void send(Session session, Object message) {
		headFilter.send(session, message);
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
