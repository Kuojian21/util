package com.tools.secret;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class MessageDigestTool {

	public enum ALGORITHM {
		MD5, SHA_1, SHA_256;
		public String toString() {
			return super.name().replace("_", "-");
		}
	}

	public static byte[] digest(ALGORITHM algorithm, String provider, byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm.toString(), provider);
			digest.update(data);
			return digest.digest();
		} catch (NoSuchProviderException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
