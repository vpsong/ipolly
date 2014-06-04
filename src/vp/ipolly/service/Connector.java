package vp.ipolly.service;

import java.net.InetSocketAddress;

/**
 * 
 * @author vp.song
 * 
 */
public interface Connector {
	
	Session connect(InetSocketAddress address);

	void disconnect();
}

