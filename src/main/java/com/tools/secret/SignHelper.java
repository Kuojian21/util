package com.tools.secret;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SignHelper {
	public static byte[] mac(byte[] datas, byte[] key) throws Exception {
		Mac mac = Mac.getInstance("HMACSHA256");
		SecretKey skey = new SecretKeySpec(key, "HMACSHA256");
		mac.init(skey);
		return mac.doFinal(datas);
	}

	public static byte[] md5(byte[] data) throws NoSuchAlgorithmException {
		if (data == null || data.length == 0) {
			return null;
		}
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(data);
		return digest.digest();
	}

	public static byte[] hmacmd5(byte[] data, int seed) {
		try {
			if (data == null || data.length == 0) {
				return null;
			}
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
			SecureRandom sr = new SecureRandom();
			sr.setSeed(seed);
			keyGen.init(64, sr);
			SecretKey skey = keyGen.generateKey();
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(skey);
			mac.update(data);
			return mac.doFinal();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
