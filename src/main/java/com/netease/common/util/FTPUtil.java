package com.netease.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * FTP工具类，需要设置变量<br>
 * ftpServerName 服务器地址<br>
 * ftpPort 服务器端口<br>
 * ftpUsername 用户名<br>
 * ftpPassword 密码<br>
 * ftpHttpUrl ftp服务器的http地址<br>
 * 
 * @author 开发支持中心
 */
public class FTPUtil
{

	/** 上传状态-创建目录失败 */
	public static final int UPLOADSTATUS_CREATE_DIRECTORY__FAIL = 1;

	/** 上传状态-创建目录成功 */
	public static final int UPLOADSTATUS_CREATE_DIRECTORY_SUCESS = 11;

	/** 上传状态-创建文件失败 */
	public static final int UPLOADSTATUS_UPLOAD_FILE__FAIL = 2;

	/** 上传状态-创建文件成功 */
	public static final int UPLOADSTATUS_UPLOAD_FILE_SUCESS = 10;

	/** 已有重名文件时，将原文件覆盖 */
	public static final int UPLOAD_TYPE_OVER = 1;

	/** 已有重名文件时，将上传内容续加到原文件后面 */
	public static final int UPLOAD_TYPE_APPEND = 2;

	/** ftp服务器地址 */
	private String ftpServerName;

	/** ftp服务器端口 */
	private int ftpPort;

	/** ftp用户名 */
	private String ftpUsername;

	/** ftp密码 */
	private String ftpPassword;

	/** ftp服务器的http地址 */
	private String ftpHttpUrl;

	private FTPClient ftpClient = new FTPClient();

	// public void initFtpUtils()
	// {
	// if (StringUtils.isBlank(getFtpServerName()))
	// {
	// PropertyUtils pu = new PropertyUtils("config", null);
	// setFtpServerName(pu.getPropery("ftpServerName"));
	// setFtpPort(Integer.parseInt(pu.getPropery("ftpPort")));
	// setFtpUsername(pu.getPropery("ftpUsername"));
	// setFtpPassword(pu.getPropery("ftpPassword"));
	// setFtpHttpUrl(pu.getPropery("ftpHttpUrl"));
	// }
	// }

	/**
	 * 连接到FTP服务器
	 * 
	 * hostname 主机名称
	 * port 端口号
	 * username 用户名
	 * password 密码
	 * @return  连接成功标识
	 * @throws IOException
	 */
	public boolean connect() throws IOException
	{
		ftpClient.connect(ftpServerName, ftpPort);
		ftpClient.setControlEncoding("UTF-8");
		if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
		{
			if (ftpClient.login(ftpUsername, ftpPassword))
			{
				return true;
			}
		}
		disconnect();
		return false;
	}

	/**
	 * 上传文件前，做预设置
	 * 
	 * @param remote
	 * @throws IOException
	 */
	private int preUpload(String remote) throws IOException
	{

		// 设置PassiveMode传输
		ftpClient.enterLocalPassiveMode();

		// 设置以二进制流的方式传输
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.setControlEncoding("UTF-8");

		// 对远程目录的处理
		if (remote.contains("/"))
		{
			// String remoteFileName = remote;
			// remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
			// 创建服务器远程目录结构，创建失败直接返回
			return CreateDirectory(remote);

		}
		else
			return UPLOADSTATUS_CREATE_DIRECTORY_SUCESS;
	}

	/**
	 * 上传文件到FTP服务器。支持多级目录嵌套，支持递归创建不存在的目录结构。
	 * 
	 * @param localFile
	 *            本地文件
	 * @param remote
	 *            要上传到FTP服务器上的文件的路径
	 * @return  上传结果标识
	 * @throws IOException
	 */
	public int upload(InputStream localFile, String remote, int type) throws IOException
	{
		if (preUpload(remote) == UPLOADSTATUS_CREATE_DIRECTORY__FAIL)
			return UPLOADSTATUS_CREATE_DIRECTORY__FAIL;
		else
			return uploadFile(remote, localFile, type);

	}

	/**
	 * 取ftp指定目录下的文件名
	 * 
	 * @param pathname 目录名称
	 * @return  文件名称数组
	 * @throws IOException
	 */
	public String[] listFileNames(String pathname) throws IOException
	{
		if (!ftpClient.isConnected() && !connect())
		{
			throw new IOException("can not connect ftp server, pls check");
		}
		return ftpClient.listNames(pathname);
	}

	/**
	 * 断开FTP服务器的链接
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException
	{
		if (ftpClient.isConnected())
		{
			ftpClient.disconnect();
		}
	}

	/**
	 * 递归创建目录
	 * 
	 * @param remote 
	 * @return  创建是否成功标识
	 * @throws IOException
	 */
	public int CreateDirectory(String remote) throws IOException
	{

		int status = UPLOADSTATUS_CREATE_DIRECTORY_SUCESS;
		String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
		if (!directory.equalsIgnoreCase("/")
				&& !ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"), "iso-8859-1")))
		{

			// 如果远程目录不存在，则递归创建远程服务器目录
			int start = 0;
			int end = 0;
			if (directory.startsWith("/"))
			{
				start = 1;
			}
			else
			{
				start = 0;
			}
			end = directory.indexOf("/", start);
			while (true)
			{
				String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
				if (!ftpClient.changeWorkingDirectory(subDirectory))
				{
					if (ftpClient.makeDirectory(subDirectory))
					{
						ftpClient.changeWorkingDirectory(subDirectory);
					}
					else
					{
						System.out.println("创建目录失败");
						return UPLOADSTATUS_CREATE_DIRECTORY__FAIL;
					}
				}

				start = end + 1;
				end = directory.indexOf("/", start);

				// 检查所有目录是否创建完毕
				if (end <= start)
				{
					break;
				}
			}
		}
		return status;
	}

	private int uploadFile(String remoteFile, InputStream localFile, int type) throws IOException
	{
		int status;

		OutputStream out = null;

		if (type == UPLOAD_TYPE_OVER)
			out = ftpClient.storeFileStream(new String(remoteFile.getBytes("UTF-8"), "iso-8859-1"));
		else if (type == UPLOAD_TYPE_APPEND)
		{
			out = ftpClient.appendFileStream(new String(remoteFile.getBytes("UTF-8"), "iso-8859-1"));
		}

		byte[] bytes = new byte[1024];
		int c;
		while ((c = localFile.read(bytes)) != -1)
		{
			out.write(bytes, 0, c);
		}
		out.flush();
		localFile.close();
		out.close();
		boolean result = ftpClient.completePendingCommand();

		status = result ? UPLOADSTATUS_UPLOAD_FILE_SUCESS : UPLOADSTATUS_UPLOAD_FILE__FAIL;

		return status;
	}

	public void deleteFile(String remote) throws IOException
	{
		if (!ftpClient.isConnected() && !connect())
		{
			throw new IOException("can not connect ftp server, pls check");
		}
		ftpClient.deleteFile(remote);
	}

	public String getFtpHttpUrl()
	{
		return ftpHttpUrl;
	}

	public void setFtpHttpUrl(String ftpHttpUrl)
	{
		this.ftpHttpUrl = ftpHttpUrl;
	}

	public String getFtpServerName()
	{
		return ftpServerName;
	}

	public void setFtpServerName(String ftpServerName)
	{
		this.ftpServerName = ftpServerName;
	}

	public int getFtpPort()
	{
		return ftpPort;
	}

	public void setFtpPort(int ftpPort)
	{
		this.ftpPort = ftpPort;
	}

	public String getFtpUsername()
	{
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername)
	{
		this.ftpUsername = ftpUsername;
	}

	public String getFtpPassword()
	{
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword)
	{
		this.ftpPassword = ftpPassword;
	}

}
