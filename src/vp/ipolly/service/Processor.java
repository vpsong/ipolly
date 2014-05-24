package vp.ipolly.service;

/**
 * 
 * @author vp.song
 * 
 */
public interface Processor {

	void doSelect();
	
	void addNew(Session session);
	
	void remove(Session session);
	
	void startup();
	
	void regWriteOps(Session session);
}

