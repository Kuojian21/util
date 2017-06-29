package com.tools.network;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;

import com.google.common.base.Strings;
import com.tools.io.IOTool;
import com.tools.logger.LogConstant;

public class FtpUtil {

	public static FTPClient ftp(String ip, int port, String username, String password) throws Exception {
		String module = "创建ftpClient";
		try {
			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(ip, port);
			ftpClient.login(username, password);
			ftpClient.setBufferSize(1024 * 1024 * 10);
			ftpClient.setControlEncoding("UTF-8");
			LogConstant.runLog.info(module, "replyCode", "replyCode=" + ftpClient.getReplyCode());
			ftpClient.changeWorkingDirectory("/");
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.setSendBufferSize(1024 * 1024 * 10);
			return ftpClient;
		} catch (Exception e) {
			LogConstant.runLog.info(module, "异常", e);
			throw e;
		}
	}

	public static void uploadFtp(FTPClient ftpClient, InputStream is, String directory, String file, boolean ftpclose,
			boolean isclose) {
		String module = "上传";
		try {
			String root = ftpClient.printWorkingDirectory();
			FtpUtil.changeDir(ftpClient, directory);
			String filename = new String(file.getBytes("UTF-8"), "ISO-8859-1");
			if (ftpClient.storeFile(filename, IOTool.buffer(is))) {
				LogConstant.runLog.info(module, "成功");
			}else{
				LogConstant.runLog.error(module, "失败");
			}
			ftpClient.changeWorkingDirectory(root);
		} catch (Exception e) {
			LogConstant.runLog.info(module, "异常", e);
		} finally {
			if (ftpclose) {
				FtpUtil.close(ftpClient);
			}
			if (isclose) {
				IOTool.close(is);
			}

		}
	}

	public static void changeDir(FTPClient ftpClient, String directory) throws IOException {
		if (Strings.isNullOrEmpty(directory)) {
		} else {
			String[] paths = directory.split("/");
			for (String path : paths) {
				if (!Strings.isNullOrEmpty(path)) {
					if (!ftpClient.changeWorkingDirectory(path)) {
						ftpClient.makeDirectory(path);
						ftpClient.changeWorkingDirectory(path);
					}
				}
			}
		}
	}

	public static void close(FTPClient ftpClient) {
		String module = "关闭ftpClient";
		if (ftpClient != null && ftpClient.isConnected()) {
			try {
				ftpClient.disconnect();
			} catch (Exception e) {
				LogConstant.runLog.info(module, "异常", e);
			}
		}
	}

}
