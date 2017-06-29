package com.tools.network;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

import com.google.common.base.Strings;
import com.tools.io.IOTool;
import com.tools.logger.LogConstant;

public class FtpsUtil {
	public static FTPSClient ftps(String ip, int port, String username, String password) throws Exception {
		String module = "创建ftpsClient";
		try {
			FTPSClient ftpClient = new FTPSClient();
			ftpClient.connect(ip, port);
			ftpClient.execPBSZ(1024l);
			ftpClient.execPROT("P"); // encrypt data channel
			ftpClient.login(username, password);
			ftpClient.setBufferSize(1024 * 1024 * 10);
			ftpClient.setControlEncoding("UTF-8");
			LogConstant.runLog.info(module, "replyCode", "replyCode=" + ftpClient.getReplyCode());
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.setSendBufferSize(1024 * 1024 * 10);
			ftpClient.enterLocalPassiveMode();
			return ftpClient;
		} catch (Exception e) {
			LogConstant.runLog.info(module, "异常", e);
			throw e;
		}
	}

	public static void uploadFtp(FTPSClient ftpClient, InputStream is, String directory, String file, boolean ftpclose,
			boolean isclose) {
		String module = "上传";
		try {
			String root = ftpClient.printWorkingDirectory();
			FtpsUtil.changeDir(ftpClient, directory);
			String filename = new String(file.getBytes("UTF-8"), "ISO-8859-1");
			if (ftpClient.storeFile(filename, IOTool.buffer(is))) {
				LogConstant.runLog.info(module, "成功");
			} else {
				LogConstant.runLog.error(module, "失败");
			}
			ftpClient.changeWorkingDirectory(root);
		} catch (Exception e) {
			LogConstant.runLog.info(module, "异常", e);
		} finally {
			if (ftpclose) {
				FtpsUtil.close(ftpClient);
			}
			if (isclose) {
				IOTool.close(is);
			}

		}
	}

	public static void downloadFtps(FTPSClient ftpClient, OutputStream os, String directory, String file,
			boolean ftpclose, boolean osclose) {
		String module = "下载";
		try {
			String root = ftpClient.printWorkingDirectory();
			FtpsUtil.changeDir(ftpClient, directory);
			BufferedOutputStream bos = IOTool.buffer(os);
			if (ftpClient.retrieveFile(file, bos)) {
				LogConstant.runLog.info(module, "成功");
			} else {
				LogConstant.runLog.error(module, "失败");
			}
			ftpClient.changeWorkingDirectory(root);
			bos.flush();
		} catch (Exception e) {
			LogConstant.runLog.info(module, "异常", e);
		} finally {
			if (ftpclose) {
				FtpsUtil.close(ftpClient);
			}
			if (osclose) {
				IOTool.close(os);
			}

		}
	}

	public static void changeDir(FTPSClient ftpClient, String directory) throws IOException {
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

	public static void close(FTPSClient ftpClient) {
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
