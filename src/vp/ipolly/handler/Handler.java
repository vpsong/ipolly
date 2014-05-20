package vp.ipolly.handler;

/**
 * 
 * @author vp.song
 *
 */
public interface Handler {

	void accepted();
	void connected();
	void received(Object message) throws Exception;
	void writed(Object message) throws Exception;
	
}
