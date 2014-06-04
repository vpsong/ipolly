package vp.ipolly.service;

public abstract class BaseSession implements Session {

	/**
	 * 收到字节数
	 */
	private long readBytes;
	
	/**
	 * 发送字节数
	 */
	private long writtenBytes;

	/**
	 * 最近一次读操作时间
	 */
	private long lastReadTime;
	
	/**
	 * 最近一次写操作时间
	 */
	private long lastWriteTime;

	public long increaseReadBytes(int increment) {
		lastReadTime = System.currentTimeMillis();
		return readBytes += increment;
	}

	public long increaseWrittenBytes(int increment) {
		lastWriteTime = System.currentTimeMillis();
		return writtenBytes += increment;
	}

	public long getReadBytes() {
		return readBytes;
	}

	public long getWrittenBytes() {
		return writtenBytes;
	}

	public long getLastReadTime() {
		return lastReadTime;
	}

	public long getLastWriteTime() {
		return lastWriteTime;
	}

}
