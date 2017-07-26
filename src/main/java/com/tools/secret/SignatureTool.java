package com.tools.secret;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class SignatureTool {
	public enum ALGORITHM {
		SHA1withDSA, SHA1withRSA, SHA256withRSA
	}

	public static boolean verify(String algorithm, String provider, PublicKey publicKey, byte[] data, byte[] sign) {
		try {
			Signature signature = Signature.getInstance(algorithm, provider);
			signature.initVerify(publicKey);
			signature.update(data);
			return signature.verify(sign);
		} catch (SignatureException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static byte[] sign(String algorithm, String provider, PrivateKey privateKey, byte[] data) throws Exception {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initSign(privateKey);
			signature.update(data);
			return signature.sign();
		} catch (SignatureException | InvalidKeyException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

	}

}
