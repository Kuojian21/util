package com.algorithm.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class MD5 {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static byte[] digest(byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5","BC");
			digest.update(data);
			return digest.digest();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		digest("node1".getBytes());
	}
}
