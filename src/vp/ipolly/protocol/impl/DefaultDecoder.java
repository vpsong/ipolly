package vp.ipolly.protocol.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import vp.ipolly.protocol.Decoder;

public class DefaultDecoder implements Decoder<DefaultMessage> {

	@Override
	public DefaultMessage decode(byte[] data) {
		if (data instanceof byte[]) {
			try {
				ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) data);
				ObjectInputStream ois = new ObjectInputStream(bis);
				Object msg = ois.readObject();
				return (DefaultMessage) msg;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}

}
