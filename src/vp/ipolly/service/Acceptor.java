package vp.ipolly.service;

/**
 * 
 * @author vp.song
 * 
 */
public interface Acceptor {
	
	void bind(int port);

	void shutdown();
}

