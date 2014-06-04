package vp.ipolly.service.tcp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import vp.ipolly.filter.FilterChain;
import vp.ipolly.handler.Handler;
import vp.ipolly.service.BaseSession;
import vp.ipolly.service.Processor;
import vp.ipolly.service.SessionState;
import vp.ipolly.service.common.Data;

/**
 * 
 * @author vpsong
 * 
 */
public class TcpSession extends BaseSession {
	
	private Logger logger = Logger
			.getLogger(TcpSession.class.getSimpleName());

	/**
	 * 新建的session状态是OPENING
	 */
	private volatile SessionState status = SessionState.OPENING;
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
	public Handler getHandler() {
		return handler;
	}

	@Override
	public void write(Object obj) {
		writeQueue.add(obj);
		processor.regWriteOps(this);
	}

	private boolean checkValid() {
		if (selectionKey.isValid()) {
			return true;
		}
		scheduledClose();
		return false;
	}

	public boolean isReadable() {
		return checkValid() && selectionKey.isReadable();
	}

	public boolean isWritable() {
		return checkValid() && selectionKey.isWritable();
	}

	public boolean isConncted() {
		if (socketChannel.isConnected()) {
			return true;
		}
		scheduledClose();
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
	
	@Override
	public void close() {
		if(status == SessionState.CLOSED) {
			return;
		}
		status = SessionState.CLOSED;
		if (selectionKey != null) {
			selectionKey.cancel();
		}
		if (socketChannel != null) {
			try {
				socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.info("close session: " + this);
	}
	
	@Override
	public void scheduledClose() {
		if(status == SessionState.CLOSING) {
			return;
		}
		status = SessionState.CLOSING;
		processor.scheduledRemove(this);
	}

	@Override
	public FilterChain getFilterChain() {
		return FilterChain.getChain();
	}

	@Override
	public int read0(ByteBuffer buffer) throws IOException {
		return socketChannel.read(buffer);
	}

	@Override
	public int write0(ByteBuffer buffer) throws IOException {
		return socketChannel.write(buffer);
	}

	@Override
	public void init() {
		try {
			socketChannel.configureBlocking(false);
			selectionKey = socketChannel.register(processor.getSelector(),
					SelectionKey.OP_READ, this);
			status = SessionState.OPENED;
		} catch (IOException e) {
			scheduledClose();
			e.printStackTrace();
		}
	}

	@Override
	public SocketAddress getRemoteAddress() throws IOException {
		return socketChannel.getRemoteAddress();
	}

	public SessionState getStatus() {
		return status;
	}

}
