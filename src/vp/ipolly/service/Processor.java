package vp.ipolly.service;

import java.nio.channels.Selector;

/**
 * 
 * @author vp.song
 * 
 */
public interface Processor {
	
	int SELECT_TIMEOUT = 1000;
	int PROCESS_IDLE_INTERVAL = 3000;
	int IDLE_TIMEOUT = 5000;

	void doSelect();
	
	void addNew(Session session);
	
	void startup();
	
	void scheduledRemove(Session session);
	
	void regWriteOps(Session session);
	
	Selector getSelector();
	
}

