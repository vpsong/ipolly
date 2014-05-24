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
		headFilter = new ReadFilter();
		BlackListFilter blackListFilter = new BlackListFilter();
		headFilter.setNextFilter(blackListFilter);
		LoggingFilter loggingFilter = new LoggingFilter();
		blackListFilter.setNextFilter(loggingFilter);
		loggingFilter.setNextFilter(new WriteFilter());
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
