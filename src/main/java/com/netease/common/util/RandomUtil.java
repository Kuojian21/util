package com.netease.common.util;

/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.DecimalFormat;

/**
 * 单例模式实现 RandomUuidFactory is a that generates random UUIDs. This
 * implementation uses the JDK's java.security.SecureRandom to generate
 * sufficiently random values for the UUIDs. <br>
 * <br>
 * This class is a singleton, so it must be constructed through the static
 * getInstance() method.
 * 
 * @author 开发支持中心
 * 
 */

public class RandomUtil {
	private static final RandomUtil _SINGLETON = new RandomUtil();

	private static final char[] _HEX_VALUES = new char[] { '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static final SecureRandom _RNG = new SecureRandom();

	private static final int N = 16;

	/**
	 * 
	 * The default constructor is explicit so we can make it private and require
	 * use of getInstance() for instantiation.
	 * 
	 * @see #getInstance()
	 * 
	 */
	private RandomUtil() {
		//
		// this is just to prevent instantiation
		//
	}

	public String createUUID() {
		return createUUID(N);
	}

	/**
	 * 
	 * @return A unique UUID of the form <em>uuid:<b>X</b></em>, where <b>X</b>
	 *         is the generated value.
	 * 
	 */
	public String createUUID(int i) {
		if (i < 1 || i > 128) {
			throw new RuntimeException("传入的参数应为1~128。param i =" + i);
		}
		//
		// first get 8 random bytes...
		//
		int ii = (i + 1) / 2;
		byte[] bytes = new byte[ii];
		_RNG.nextBytes(bytes);

		StringBuffer uuid = new StringBuffer(i + 2);

		//
		// ...then we need to shift so they're valid hex (0-9, a-f)
		//
		for (int n = 0; n < ii; ++n) {

			// if (n == 4)uuid.append('-');

			//
			// shift the bits so they are proper hex
			//
			int hex = bytes[n] & 255;
			uuid.append(_HEX_VALUES[hex >> 4]);
			uuid.append(_HEX_VALUES[hex & 15]);
		}

		return uuid.substring(uuid.length() - i);
	}

	/**
	 * 获取单例对象
	 * 
	 * @return The singleton instance of this class.
	 * 
	 */
	public static RandomUtil getInstance() {
		return _SINGLETON;
	}
	public static final String allCharWithoutOL = "23456789abcdefghijkmnpqrstuvwxyz";
	public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String numberChar = "0123456789";

	public static String generateString(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(_RNG.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	public static String generateMixString(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(_RNG.nextInt(letterChar.length())));
		}
		return sb.toString();
	}
	/**
	 * 生成指定长度的数字字符串
	 * @param length
	 * @return
	 */
	public static String generateNumberString(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(numberChar.charAt(_RNG.nextInt(numberChar.length())));
		}
		return sb.toString();
	}
    /**
     * 生成小写字符串
     * @param length
     * @return
     */
	public static String generateLowerString(int length) {
		return generateMixString(length).toLowerCase();
	}
    /**
     * 生成大写字母组成的字符串
     * @param length
     * @return
     */
	public static String generateUpperString(int length) {
		return generateMixString(length).toUpperCase();
	}

	public static String generateZeroString(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append('0');
		}
		return sb.toString();
	}

	public static String toFixdLengthString(long num, int fixdlenth) {
		StringBuffer sb = new StringBuffer();
		String strNum = String.valueOf(num);
		if (fixdlenth - strNum.length() >= 0) {
			sb.append(generateZeroString(fixdlenth - strNum.length()));
		} else {
			throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth
					+ "的字符串发生异常!");
		}
		sb.append(strNum);
		return sb.toString();
	}

	public static String toFixdLengthString(int num, int fixdlenth) {
		StringBuffer sb = new StringBuffer();
		String strNum = String.valueOf(num);
		if (fixdlenth - strNum.length() >= 0) {
			sb.append(generateZeroString(fixdlenth - strNum.length()));
		} else {
			throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth
					+ "的字符串发生异常!");
		}
		sb.append(strNum);
		return sb.toString();
	}
	/**电影票随机优惠码生成*/
	public static String generateVoucodeString(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(allCharWithoutOL.charAt(_RNG.nextInt(allCharWithoutOL.length())));
		}
		return sb.toString();
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		RandomUtil factory = RandomUtil.getInstance();
//        System.out.println(RandomUtil.generateNumberString(3));
//        System.out.println(factory.generateNumberString(3));
//        System.out.println(new BigDecimal("16").pow(10));
        DecimalFormat format = new DecimalFormat("00000");
//		System.out.println(format.format(123L%100000));
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			/**优惠码*/
			//insert into tb_shop_code values('e2d278c9d7',1,sysdate,null,null,'测试兑换券5元',null,sysdate,1,1,null);
//			String randCode = RandomUtil.generateVoucodeString(10);
//			String sql = "insert into tb_shop_code(voucode_id,status,create_time,update_time,coupon_id,activity_id) " +
//			"values('"+randCode+"',1,sysdate,sysdate,11,null);";
//			System.out.println(sql);
//			sb.append(sql+"\n");			
//			sb2.append(randCode+"\n");	
			/**通兑码*/
			//insert into tb_shop_rebate_code(rebate_id,rebate_rule_id,status,create_time) 
			//values('e2d278c9d7',1,sysdate,null,null,'测试兑换券5元',null,sysdate,1,1,null);
			String randCode = RandomUtil.generateVoucodeString(12);
			String sql = "insert into tb_shop_rebate_code(rebate_id,rebate_rule_id,status,create_time) " +
			"values('"+randCode+"',1,1,sysdate);";
			System.out.println(sql);
			sb.append(sql+"\n");			
			sb2.append(randCode+"\n");	
		}
		FileUtil.write("10ruomi_rebate_code_test.sql", sb.toString().getBytes(), false);
		FileUtil.write("10ruomi_rebate_code_test.txt", sb2.toString().getBytes(), false);
		
		String randCode = RandomUtil.generateMixString(16);
		System.out.print(randCode);
		
	}
}
