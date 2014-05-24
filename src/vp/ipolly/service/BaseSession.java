package vp.ipolly.service;

public abstract class BaseSession implements Session {

	private long readBytes;
	private long writtenBytes;

	private long lastReadTime;
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
