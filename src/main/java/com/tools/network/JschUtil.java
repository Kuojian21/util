package com.tools.network;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;
import com.tools.io.StreamTool;
import com.tools.logger.LogConstant;

public class JschUtil {

	private static final Pattern SPLIT = Pattern.compile("^SPLIT_\\w+:");

	public static void uploadSftp(ChannelSftp sftp, InputStream is, String directory, String file,boolean sftpclose,boolean isclose) {
		String module = "上传";
		try {
			String root = sftp.pwd();
			JschUtil.changeDir(sftp, directory);
			sftp.put(StreamTool.buffer(is), file);
			sftp.cd(root);
			LogConstant.runLog.info(module, "结束", "file:" + file);
		} catch (Exception e) {
			LogConstant.runLog.fatal(module, "异常", e, "file:" + file);
		} finally{
			if(sftpclose){
				JschUtil.close(sftp);
			}
			if(isclose){
				StreamTool.close(is);
			}
		}
	}

	public static void downloadSftp(ChannelSftp sftp, OutputStream os, String directory, String file,boolean sftpclose,boolean osclose) {
		String module = "下载";
		try {
			String root = sftp.pwd();
			JschUtil.changeDir(sftp, directory);
			BufferedOutputStream bos = StreamTool.buffer(os);
			sftp.get(file, bos);
			bos.flush();
			sftp.cd(root);
			LogConstant.runLog.info(module, "结束", "file:" + file);
		} catch (Exception e) {
			LogConstant.runLog.fatal(module, "异常", e, "file:" + file);
		} finally{
			if(sftpclose){
				JschUtil.close(sftp);
			}
			if(osclose){
				StreamTool.close(os);
			}
		}
	}
	

	public static void changeDir(ChannelSftp sftp, String directory) throws SftpException {
		String module = "修改目录";
		if (!Strings.isNullOrEmpty(directory)) {
			String[] dirs = directory.split("/");
			for (String dir : dirs) {
				if (Strings.isNullOrEmpty(dir)) {
					continue;
				}
				Vector<?> vector = sftp.ls(".");
				boolean exist = false;
				for (Object item : vector) {
					LsEntry entry = (LsEntry) item;
					if (dir.equals(entry.getFilename())) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					sftp.mkdir(dir);
				}
				sftp.cd(dir);
			}
		}
		LogConstant.runLog.info(module, "结束", "dir=" + sftp.pwd());
	}
	

	public static ChannelSftp sftp(String host, int port, String username, String password) {
		String module = "获得ChannelSftp";
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			Session sshSession = jsch.getSession(username, host, port);
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		} catch (Exception e) {
			LogConstant.runLog.error(module, "异常", e);
		}
		return sftp;
	}
	

	public static ChannelSftp sftp(String host, int port, String username, String prvfile, String pubfile,
			byte[] passphrase) {
		String module = "获得ChannelSftp";
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			jsch.addIdentity(getPath(prvfile), getPath(pubfile), passphrase);
			Session sshSession = jsch.getSession(username, host, port);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		} catch (Exception e) {
			LogConstant.runLog.error(module, "异常", e);
		}
		return sftp;
	}

	public static void close(ChannelSftp sftp) {
		String module = "关闭channel";
		if (sftp != null) {
			try {
				sftp.disconnect();
				sftp.getSession().disconnect();
			} catch (JSchException e) {
				LogConstant.runLog.error(module, "异常", e);
			}
		}
	}
	

	public static String getPath(String src) {
		String classpath = "classpath:";
		if (src.startsWith(classpath)) {
			src = src.substring(classpath.length());
			return JschUtil.class.getResource("/" + src).getFile();
		} else {
			Matcher sMatcher = SPLIT.matcher(src);
			if (sMatcher.find()) {
				String prefix = sMatcher.group();
				src = src.substring(prefix.length());
				return JschUtil.class.getResource("/").getFile().split(prefix.substring(6, prefix.length() - 1))[0]
						+ src;
			}

		}
		return src;
	}

}
