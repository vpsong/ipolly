package vp.ipolly.filter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import vp.ipolly.service.Session;
import vp.ipolly.service.common.CommonUtil;
import vp.ipolly.service.common.Data;

public class HeartbeatFilter extends FilterAdaptor {

	private Logger logger = Logger.getLogger(HeartbeatFilter.class
			.getSimpleName());

	private final byte[] hbReq = new byte[] { -1, 0, -1 };
	private final byte[] hbResp = new byte[] { -2, 0, -2 };

	private boolean isHbRequest(Data msg) {
		return CommonUtil.equal(hbReq, (byte[]) msg.getData());
	}

	private boolean isHbResponse(Data msg) {
		return CommonUtil.equal(hbResp, (byte[]) msg.getData());
	}

	@Override
	public void idle(Session session) {
		logger.info("heart beat request");
		try {
			session.write0(ByteBuffer.wrap(hbReq));
			session.increaseWrittenBytes(hbReq.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void read(Session session, Object message) {
		if (isHbRequest((Data) message)) {
			logger.info("heart beat response");
			try {
				session.write0(ByteBuffer.wrap(hbResp));
				session.increaseWrittenBytes(hbResp.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		if (isHbResponse((Data) message)) {
			return;
		}
		nextFilter().read(session, message);
	}
}
