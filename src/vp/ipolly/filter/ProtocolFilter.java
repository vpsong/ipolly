package vp.ipolly.filter;

import vp.ipolly.protocol.Decoder;
import vp.ipolly.protocol.Encoder;
import vp.ipolly.protocol.impl.DefaultDecoder;
import vp.ipolly.protocol.impl.DefaultEncoder;
import vp.ipolly.protocol.impl.DefaultMessage;
import vp.ipolly.service.Session;
import vp.ipolly.service.common.Data;

public class ProtocolFilter extends FilterAdaptor {

	private Encoder<DefaultMessage> encoder = new DefaultEncoder();
	private Decoder<DefaultMessage> decoder = new DefaultDecoder();

	@Override
	public void read(Session session, Object message) {
		Data data = (Data) message;
		Object obj = decoder.decode((byte[]) data.getData());
		nextFilter().read(session, obj);
	}

	@Override
	public void send(Session session, Object message) {
		Data data = (Data) message;
		nextFilter().send(session, data.getData());
		data.setData(encoder.encode((DefaultMessage)data.getData()));
	}

}
