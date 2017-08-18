package com.test.ftp;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.tools.io.StreamTool;
import com.tools.network.JschUtil;

public class JschUtilTest {

	@Test
	public void upload1() {
		for (int i = 0; i < 10; i++) {
			JschUtil.uploadSftp(JschUtil.sftp("trans.kaifae.com", 2022, "LMLC", "9X9XWa$f"),
					StreamTool.toInputStream(new String("Hello World!").getBytes()),
					"/upload/test", "test" + i + ".txt", true, true);
		}

	}

	@Test
	public void upload2() throws UnsupportedEncodingException {
		JschUtil.uploadSftp(JschUtil.sftp("123.57.157.2", 22, "lmlctest",
				"SPLIT_classes:classes/exchange_njjjs.pub",
				"SPLIT_classes:classes/exchange_njjjs.ppk", "njjjs".getBytes("UTF-8")),
				StreamTool.toInputStream(new String("Hello World!123").getBytes()),
				"upload", "test.txt", true, true);
	}

}
