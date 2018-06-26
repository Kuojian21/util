package com.test.runtime;

import java.io.IOException;

public class RuntimeTest {
	public static void main(String[] args) throws IOException, InterruptedException {
		String command = "/Users/kuojian21/make_video.sh";
		String template = "All-4";
		String name= "6798625349-All-4-1280x1280-20180623-19-59-112";
		String videoCount = "1";
		String srcs = "\"http://adsconsole.corp.kuaishou.com/api/download/photo/video?photoId=6798625349\"";
		String starttimes = "0";
		String durations = "10";
		String resolutions = "900:1280";
		String[] commands = new String[] {
			command,
			template,
			name,
			videoCount,
			srcs,
			starttimes,
			durations,
			resolutions,
			">> /Users/kuojian21/make_video.log",
			"2>&1"
			
		};
		Process process = Runtime.getRuntime().exec(commands);
		System.out.println(process.waitFor());
	}
}
