package vp.ipolly.protocol.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import vp.ipolly.protocol.Encoder;

public class DefaultEncoder implements Encoder<DefaultMessage> {

	@Override
	public byte[] encode(DefaultMessage msg) {
		byte[] bytes = new byte[msg.getLength()];
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(msg);
			oos.flush();
			bytes = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

}
