package vp.ipolly.handler;

/**
 * 
 * @author vp.song
 *
 */
public class DefaultHandlerImpl extends HandlerAdapter {
	
	@Override
	public void accepted() {
		System.out.println("accepted a client");
	}
	
	@Override
	public void connected() {
		System.out.println("connected to server");
	}

	@Override
	public void received(Object message) throws Exception {
		System.out.println("recieve: " + message);
	}

	@Override
	public void writed(Object message) throws Exception {
		System.out.println("write: " + message);
	}
	
}
