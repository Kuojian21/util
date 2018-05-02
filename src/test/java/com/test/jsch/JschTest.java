package com.test.jsch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.jsch.Jsch;
import com.tools.io.StreamTool;

public class JschTest {

	@Test
	public void upload() throws FileNotFoundException {
		Jsch sch = new Jsch("trans.kaifae.com", 2022, "LMLC", "9X9XWa$f");
		if (sch.upload("/upload/testxy/xxxx", "test" + ".txt",
				StreamTool.toInputStream(new String("Hello World!").getBytes()))) {
			sch.download("/upload/testxy/xxxx", "test" + ".txt", new FileOutputStream(new File("test.txt")));
			System.out.println(new File("test.txt").getAbsolutePath());
		}
	}

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
