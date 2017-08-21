package com.tools.file;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Strings;

/**
 * http://docs.oracle.com/javase/tutorial/essential/io/notification.html
 */

public class FileTool {
	
	public static boolean mkdir(String dir) {
		if (Strings.isNullOrEmpty(dir)) {
			return false;
		}
		File p = new File(dir);
		if (!p.exists()) {
			return p.mkdirs();
		}
		return true;
	}

	public static boolean createFile(String file) {
		try {
			if (Strings.isNullOrEmpty(file)) {
				return false;
			}
			File p = new File(file);
			if (!p.exists()) {
				return p.createNewFile();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {

		}

	}

}
