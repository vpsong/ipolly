package vp.ipolly.filter;

import vp.ipolly.service.Session;

/**
 * 
 * @author vpsong
 * 
 */
public class FilterAdaptor implements Filter {

	private Filter nextFilter;

	@Override
	public void read(Session session, Object message) {
		if (nextFilter != null) {
			nextFilter.read(session, message);
		}
	}

	@Override
	public void send(Session session, Object message) {
		if (nextFilter != null) {
			nextFilter.send(session, message);
		}
	}

	@Override
	public Filter nextFilter() {
		return nextFilter;
	}

	public void setNextFilter(Filter nextFilter) {
		this.nextFilter = nextFilter;
	}

}
