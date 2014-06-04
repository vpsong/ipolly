package vp.ipolly.service;

import java.nio.channels.Selector;

/**
 * 
 * @author vp.song
 * 
 */
public interface Processor {
	
	/**
	 * select阻塞时间
	 */
	int SELECT_TIMEOUT = 1000;
	
	/**
	 * 空闲检查间隔
	 */
	int PROCESS_IDLE_INTERVAL = 3000;
	
	/**
	 * 进入空闲状态时间
	 */
	int IDLE_TIMEOUT = 5000;

	void doSelect();
	
	void addNew(Session session);
	
	void startup();
	
	void scheduledRemove(Session session);
	
	void regWriteOps(Session session);
	
	Selector getSelector();
	
}

