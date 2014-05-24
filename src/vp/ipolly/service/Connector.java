package vp.ipolly.service;

/**
 * 
 * @author vp.song
 * 
 */
public interface Connector {
	
	Session connect();

	void shutdown();
}

