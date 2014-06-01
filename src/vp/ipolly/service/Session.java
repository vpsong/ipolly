package vp.ipolly.service;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import vp.ipolly.filter.FilterChain;
import vp.ipolly.handler.Handler;
import vp.ipolly.service.common.Data;

/**
 * 
 * @author vpsong
 * 
 */
public interface Session {

	void init();

	Handler getHandler();

	SelectionKey getSelectionKey();

	void setSelectionKey(SelectionKey selectionKey);

	Queue<Object> getWriteQueue();

	Processor getProcessor();

	void setProcessor(Processor processor);

	boolean isReadable();

	boolean isWritable();

	void write(Object msg);

	boolean isConncted();

	void close();

	void scheduledClose();

	FilterChain getFilterChain();

	long increaseReadBytes(int increment);

	long increaseWrittenBytes(int increment);

	long getReadBytes();

	long getWrittenBytes();

	long getLastReadTime();

	long getLastWriteTime();

	int read0(ByteBuffer buffer) throws IOException;

	int write0(ByteBuffer buffer) throws IOException;

	SocketAddress getRemoteAddress() throws IOException;

	SessionState getStatus();
}
