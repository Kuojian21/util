package com.test.runtime;

import java.io.IOException;

public class RuntimeTest {
	public static void main(String[] args) throws IOException, InterruptedException {
		for(int i = 0; i< 100;i++) {
			long s = System.currentTimeMillis();
			String command = "/data/app/vidio-generater/bin/make_video.sh";
			String template = "All-4";
			String name= "6798625349-All-4-1280x1280-20180623-19-59-112";
			String videoCount = "1";
			String srcs = "";
			String starttimes = "0 0";
			String durations = "10 10";
			String resolutions = "900:1280 900:1280";
			String[] commands = new String[] {
				command,
				template,
				name,
				videoCount,
				srcs,
				starttimes,
				durations,
				resolutions
			};
			Process process = Runtime.getRuntime().exec(commands);
			System.out.println(process.waitFor());
			System.out.println((System.currentTimeMillis() - s) /1000 + "s");
		}
	}
}
