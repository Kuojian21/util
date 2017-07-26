package com.tools.secret;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MacTool {

	public enum ALGORITHM {
		HmacMD5, HmacSHA1, HmacSHA256;
	}
	
	public static byte[] sign(ALGORITHM algorithm, String provider, byte[] data, byte[] key) {
		try {
			Mac mac = Mac.getInstance(algorithm.name());
			SecretKey skey = new SecretKeySpec(key, algorithm.name());
			mac.init(skey);
			mac.update(data);
			return mac.doFinal();
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
			return null;
		}
	}

}
