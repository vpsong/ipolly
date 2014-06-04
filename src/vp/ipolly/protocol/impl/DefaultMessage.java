package vp.ipolly.protocol.impl;

import java.io.Serializable;

/**
 * 默认消息格式
 * @author vpsong
 *
 */
public class DefaultMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String data;
	private int length;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public String toString() {
		return "data: " + data + " ; length: " + length;
	}

}
