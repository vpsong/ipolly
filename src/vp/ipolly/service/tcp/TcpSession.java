package vp.ipolly.service.tcp;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import vp.ipolly.handler.Handler;
import vp.ipolly.service.Processor;
import vp.ipolly.service.Session;

/**
 * 
 * @author vpsong
 * 
 */
public class TcpSession implements Session {

	private SocketChannel socketChannel;
	private Handler handler;
	private Queue<Object> writeQueue;
	private SelectionKey selectionKey;
	private Processor processor;

	public TcpSession(SocketChannel socketChannel, Handler handler) {
		this.socketChannel = socketChannel;
		this.handler = handler;
		this.writeQueue = new ConcurrentLinkedQueue<Object>();
	}

	@Override
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	@Override
	public Handler getHandler() {
		return handler;
	}

	public void write(Object obj) {
		writeQueue.add(obj);
		processor.regWriteOps(this);
	}
	
	private boolean checkValid() {
		if(selectionKey.isValid()) {
			return true;
		}
		processor.remove(this);
		return false;
	}
	
	public boolean isReadable() {
		return checkValid() && selectionKey.isReadable();
	}
	
	public boolean isWritable() {
		return checkValid() && selectionKey.isWritable();
	}
	
	public boolean isConncted() {
		if(socketChannel.isConnected()) {
			return true;
		}
		processor.remove(this);
		return false;
	}

	public SelectionKey getSelectionKey() {
		return selectionKey;
	}

	public void setSelectionKey(SelectionKey selectionKey) {
		this.selectionKey = selectionKey;
	}

	public Queue<Object> getWriteQueue() {
		return writeQueue;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}
	
}
