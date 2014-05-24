package vp.ipolly.filter;

import vp.ipolly.service.Session;

public interface Filter {

	void read(Session session, Object message);
	
	void send(Session session, Object message);
	
	Filter nextFilter();
	
	void setNextFilter(Filter nextFilter);
}
