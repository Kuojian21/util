package com.tools.secret;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class EncodeHelper {

	private static final char[] HEXCHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
			'b', 'c', 'd', 'e', 'f' };

	public static byte[] toHex(byte[] datas) {
		return new Hex().encode(datas);
	}

	public static String toHex2(byte data) {
		StringBuilder builder = new StringBuilder();
		builder.append(HEXCHAR[(data & 0xf0) >>> 4]);
		builder.append(HEXCHAR[(data & 0x0f)]);
		return builder.toString();
	}

	public static String base64(byte[] datas) {
		return Base64.encodeBase64String(datas);
	}

	
}
