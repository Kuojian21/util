package com.crypt;

import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.google.common.base.Strings;

public class Encrypt extends Crypt {

	private GenericObjectPool<Cipher> pool;

	public Encrypt(String algorithm, String padding, String keyAlgorithm, String keyValue) {
		super(algorithm, padding, keyAlgorithm, keyValue);
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(10);
		config.setMaxTotal(100);
		config.setMaxWaitMillis(30000);
		pool = new GenericObjectPool<>(new BasePooledObjectFactory<Cipher>() {
			@Override
			public PooledObject<Cipher> wrap(Cipher cipher) {
				return new DefaultPooledObject<Cipher>(cipher);
			}

			@Override
			public Cipher create() throws Exception {
				Cipher cipher = Cipher.getInstance(Encrypt.this.getAlgorithm());
				cipher.init(Cipher.ENCRYPT_MODE, Encrypt.this.getKey(), Encrypt.this.getIvp());
				return cipher;
			}

		}, config);
	}

	public String encrypt(String str) {
		Cipher cipher = null;
		try {
			cipher = pool.borrowObject();
			if (Strings.isNullOrEmpty(str)) {
				return null;
			}
			byte[] data = Base64.encodeBase64(cipher.doFinal(str.getBytes("UTF-8")));
			return new String(data, "UTF-8");
		} catch (Exception e) {
			return null;
		} finally {
			if (cipher != null) {
				pool.returnObject(cipher);
			}
		}
	}

}
