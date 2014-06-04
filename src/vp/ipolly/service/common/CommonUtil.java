package vp.ipolly.service.common;

public class CommonUtil {

	/**
	 * 字节数组相等判断
	 * @param b1
	 * @param b2
	 * @return
	 */
	public static boolean equal(byte[] b1, byte[] b2) {
		if (b1.length != b2.length) {
			return false;
		}
		if (b1.length == 0 || b2.length == 0) {
			return false;
		}
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}
}
