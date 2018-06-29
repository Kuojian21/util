package com.test.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WriterTest {
	public static void main(String[] args) throws IOException {
		for(int i = 0;i < 10;i++) {
			PrintWriter bw = new PrintWriter(new FileWriter("task.id")); 
			bw.println(i + "");
			bw.close();
		}
	}
}
