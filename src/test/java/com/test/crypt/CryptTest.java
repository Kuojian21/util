package com.test.crypt;

import java.util.List;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.crypt.Decrypt;
import com.crypt.Encrypt;
import com.google.common.collect.Lists;

public class CryptTest {

	public static void main(String[] args) {
		try {
			Encrypt encrypt = new Encrypt("DESede/CBC/PKCS5Padding", "01234567", "DESede",
					"wnwT1v1kkhoEwnnEGSnE6ciw6S8E4w5U");
			Decrypt decrypt = new Decrypt("DESede/CBC/PKCS5Padding", "01234567", "DESede",
					"wnwT1v1kkhoEwnnEGSnE6ciw6S8E4w5U");
			GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			config.setMinIdle(10);
			config.setMaxTotal(100);
			config.setMaxWaitMillis(30000);
			GenericObjectPool<Cipher> pool = new GenericObjectPool<>(new BasePooledObjectFactory<Cipher>() {
				@Override
				public PooledObject<Cipher> wrap(Cipher cipher) {
					return new DefaultPooledObject<Cipher>(cipher);
				}

				@Override
				public Cipher create() throws Exception {
					Cipher cipher = Cipher.getInstance(encrypt.getAlgorithm());
					cipher.init(Cipher.ENCRYPT_MODE, encrypt.getKey(), encrypt.getIvp());
					return cipher;
				}

			}, config);
			Cipher cipher = null;
			long b = System.currentTimeMillis();
			for (int i = 0; i < 10000; i++) {
				cipher = Cipher.getInstance(encrypt.getAlgorithm());
				cipher.init(Cipher.DECRYPT_MODE, encrypt.getKey(), encrypt.getIvp());
			}
			System.out.println(System.currentTimeMillis() - b);
			b = System.currentTimeMillis();
			for (int i = 0; i < 10000; i++) {
				cipher = pool.borrowObject();
				pool.returnObject(cipher);
			}
			System.out.println(System.currentTimeMillis() - b);

			List<String> data = Lists.newArrayList();
			for (int i = 0; i < 1000; i++) {
				data.add(encrypt.encrypt("kuojianssdAFsasfARWRERWEQTWQR" + i));
			}
			for (String s : data) {
				System.out.println(decrypt.decrypt(s));
			}

			Cipher e = Cipher.getInstance(encrypt.getAlgorithm());
			e.init(Cipher.ENCRYPT_MODE, encrypt.getKey(), encrypt.getIvp());
			Cipher d = Cipher.getInstance(encrypt.getAlgorithm());
			d.init(Cipher.DECRYPT_MODE, encrypt.getKey(), encrypt.getIvp());

			System.out.println(decrypt.decrypt("ZzbSDNu5gro="));;
			
			for (int i = 0; i < 10; i++) {
				/*new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							String str = encrypt.encrypt("kuojian");
							System.out.println(decrypt.decrypt(str));
						}
					}
				}).start();*/
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							try {
								String str = Base64.encodeBase64String(e.doFinal("kuojian".getBytes("UTF-8")));
								System.out.println(str);
								System.out.println(new String(d.doFinal(Base64.decodeBase64(str)), "UTF-8"));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
			}
		} catch (Exception e) {
		}
	}
}
