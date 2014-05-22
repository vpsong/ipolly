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
	public int read(Session session, Object message) {
		return 0;
	}

	@Override
	public int send(Session session, Object message) {
		return 0;
	}

	@Override
	public Filter nextFilter() {
		return nextFilter;
	}

	public void setNextFilter(Filter nextFilter) {
		this.nextFilter = nextFilter;
	}

}
