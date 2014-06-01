package vp.ipolly.filter;

import vp.ipolly.service.Session;

public interface Filter {

	void read(Session session, Object message);
	
	void send(Session session, Object message);
	
	void idle(Session session);
	
	Filter nextFilter();
	
	void setNextFilter(Filter nextFilter);
}
