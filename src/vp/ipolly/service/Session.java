package vp.ipolly.service;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import vp.ipolly.handler.Handler;

/**
 * 
 * @author vpsong
 * 
 */
public interface Session {

	SocketChannel getSocketChannel();

	Handler getHandler();

	SelectionKey getSelectionKey();

	void setSelectionKey(SelectionKey selectionKey);

	Queue<Object> getWriteQueue();

	Processor getProcessor();

	void setProcessor(Processor processor);

	boolean isReadable();

	boolean isWritable();
	
	void write(Object obj);
	
	boolean isConncted();
}
