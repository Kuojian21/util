package com.tools.secret;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


import com.itextpdf.text.pdf.codec.Base64;
import com.tools.io.IOTool;

public class KeyFactoryTool {
	public enum ALGORITHM {
		DiffieHellman, DSA, RSA;
	}
	
	
	public KeyFactory keyFactory(ALGORITHM algorithm, String provider) {
		try {
			return KeyFactory.getInstance(algorithm.name(), provider);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PublicKey publicKey(KeyFactory keyFactory, byte[] key) {
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
			return keyFactory.generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}

	public PrivateKey privateKey(KeyFactory keyFactory, byte[] key) {
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
			return keyFactory.generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] loadKey(Reader reader) {
		try {
			BufferedReader br = IOTool.buffer(reader);
			StringBuilder sb = new StringBuilder();
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			return Base64.decode(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			IOTool.close(reader);
		}
	}
	
}
