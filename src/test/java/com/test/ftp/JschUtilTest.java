package com.test.ftp;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.jsch.Jsch;
import com.tools.io.StreamTool;

public class JschUtilTest {

	@Test
	public void upload1() {
		Jsch sch = new Jsch("trans.kaifae.com", 2022, "LMLC", "9X9XWa$f");
		sch.upload("/upload/test", "test" + ".txt", StreamTool.toInputStream(new String("Hello World!").getBytes()));
	}

	@Test
	public void upload2() throws UnsupportedEncodingException {
		Jsch sch = new Jsch("123.57.157.2", 22, "lmlctest", "SPLIT_classes:classes/exchange_njjjs.pub",
				"SPLIT_classes:classes/exchange_njjjs.ppk", "njjjs".getBytes("UTF-8"));
		try {
			sch.upload("upload", "test.txt", StreamTool.toInputStream(new String("Hello World!123").getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
