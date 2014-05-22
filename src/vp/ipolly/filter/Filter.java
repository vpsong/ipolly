package vp.ipolly.filter;

import vp.ipolly.service.Session;

public interface Filter {

	int read(Session session, Object message);
	
	int send(Session session, Object message);
	
	Filter nextFilter();
	
	void setNextFilter(Filter nextFilter);
}
