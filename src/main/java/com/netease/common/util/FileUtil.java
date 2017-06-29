package com.netease.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;


/**
 * 文件读写工具类，主要封装了如下方法：<br/>
 * 1.文件路径获取；<br/>
 * 2.文件读取;<br/>
 * 3.写文件;<br/>
 * 4.zip打包文件;<br/>
 * 5.解压zip文件等功能。<br/>
 *
 * @author 开发支持中心
 */
public class FileUtil {
	
	private static Logger logger = Logger.getLogger(FileUtil.class);
	
	public final static String WINDOWS = "Windows";
	
	public final static String LINUX = "Linux";
	
	/***************************************************************************
	 * 判断操作系统
	 * 
	 * @return
	 *       操作系统字符串
	 *************************************************************************/
	public static String getOS() {

		if (File.separator.equalsIgnoreCase("\\"))
			return WINDOWS;
		else
			return LINUX;
	}
	
	/**
	 * 得到临时文件夹，如果没有则自动创建一个
	 * 
	 * @return
	 *       临时文件对象
	 */
	
	public static File getImageTempDir() {

		return getDir(getImageTempPath(), true);
	}
	
	public static String getImageTempPath() {

		if (getOS().equalsIgnoreCase(WINDOWS)) {
			return PropertyUtil.getProperty("image.temp.dir");
		}
		return PropertyUtil.getProperty("linux.image.temp.dir");
	}
	
	public static File getImageSaveDir() {

		return getDir(getImageSavePath(), true);
	}
	
	public static String getImageSavePath() {

		if (getOS().equalsIgnoreCase(WINDOWS)) {
			return PropertyUtil.getProperty("image.dir");
		}
		return PropertyUtil.getProperty("linux.image.dir");
	}
	
	/**
	 * 返回指定文件夹中的指定文件
	 * 
	 * @param dir    文件路径
	 * @param fileName 文件名称
	 * @param create 是否新创建
	 * @return 文件对象
	 */
	public static File getFile(File dir, String fileName, boolean create) {

		File file = new File(dir.getAbsolutePath() + File.separator + fileName);
		try {
			if (create && !file.exists()) {
				file.createNewFile();
			}
		} catch (IOException ioe) {
			logger.error("Could not create file {}." + file.getPath());
			return null;
		}
		return file;
	}
	
	public static String getInitDirPath() {

		if (getOS().equalsIgnoreCase(WINDOWS)) {
			return PropertyUtil.getProperty("init.dir");
		}
		return PropertyUtil.getProperty("linux.init.dir");
	}
	
	public static File getInitDir() {

		return getDir(getInitDirPath(), true);
	}
	
	/**
	 * TODO 没有做错误处理，例如指定路径是一个文件路径 返回指定路径下的目录
	 * 
	 * @param path
	 *            指定路径
	 * @param create
	 *            如果没有则创建
	 * @return
	 * 			  文件对象
	 */
	public static File getDir(String path, boolean create) {

		File dir = new File(path);
		if (!dir.exists() && create) {
			System.out.println("dir==" + dir);
			dir.mkdirs();
		}
		return dir;
	}
	
	/***
	 * 创建文件夹下文件
	 * 
	 * @param dirPath 文件路径
	 * @param fileName 文件名称
	 * @return  文件对象
	 * @throws Exception
	 */
	public static File getFileByDirAndFile(String dirPath, String fileName) {

		File file = null;
		try {
			File dirFile = new File(dirPath);
			if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
				boolean creadok = dirFile.mkdirs();
				if (creadok) {
					logger.info("ok:create dir success! ");
				} else {
					logger.error("err:create dir fail!");
				}
			}
			file = new File(dirPath.concat(File.separator).concat(fileName));
			if (!file.exists()) {
				boolean creafok = file.createNewFile();
				if (creafok) {
					logger.info(" ok:create file success! ");
				} else {
					logger.error(" err:create file fail!");
				}
			}
			logger.info(" ok: file exists! ");
		} catch (Exception e) {
			e.printStackTrace();
			return file;
		}
		return file;
	}
	/**
	 * 以指定的编码方式获取文件内容
	 * @param file 文件对象
	 * @param encode 编码
	 * @return 文件的BufferedReader对象
	 * @throws IOException
	 */
	public BufferedReader getFileContent(File file, String encode) throws IOException {

		InputStreamReader isr = new InputStreamReader(new FileInputStream(file), encode);
		BufferedReader read = new BufferedReader(isr);
		return read;
	}
	/**
	 * 以默认的编码方式获取文件内容
	 * @param file 文件对象
	 * @return  文件BufferedReader对象
	 * @throws IOException
	 */
	public BufferedReader getFileContent(File file) throws IOException {

		FileInputStream fin = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fin));
		return br;
	}
	
	public static String getBasePath() {

		String filePath = FileUtil.class.getResource("/").getPath();
		return filePath;
	}
	
	/**
	 * 获取单一文件的md5值
	 * 
	 * @param file 文件对象
	 * @return
	 *        文件的MD5摘要值
	 */
	public static String getFileMD5(File file) {

		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}
	
	/**
	 * 读取文件内容
	 * 
	 * @param fileName 文件名称
	 * @return 文件内容
	 * @throws FileNotFoundException
	 */
	public static String read(String fileName) throws FileNotFoundException, IOException {

		String s = null;
		StringBuffer sb = null;
		
		File f = new File(fileName);
		if (f.exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			try {
				while ((s = br.readLine()) != null) {
					if (sb == null) {
						sb = new StringBuffer();
					}
					sb.append(s);
				}
			} finally {
				br.close();
			}
			return sb.toString();
		}
		return null;
	}
	
	/**
	 * 解压缩zip文件
	 * 
	 * @param zipFileName
	 * @param targetDir
	 * @throws IOException
	 */
	public static void unzipFile(String zipFileName, String targetDir) throws IOException {

		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFileName)));
		ZipEntry zipEntry;
		byte[] buf = new byte[1024];
		while ((zipEntry = zis.getNextEntry()) != null) {
			File file = new File(targetDir + File.separator + zipEntry.getName());
			FileOutputStream fos = new FileOutputStream(file);
			int readBytes;
			while ((readBytes = zis.read(buf)) > 0) {
				fos.write(buf, 0, readBytes);
			}
			fos.close();
		}
		zis.closeEntry();
		zis.close();
	}
	
	/**
	 * 写文件。如果文件不存在，创建文件；如果存在，重写文件或把内容如附在末尾。
	 * 
	 * @param fileName
	 *            全路径文件名。
	 * @param content
	 *            文件内容。
	 * @param append
	 *            false:从文件开始写。true：把content添加到文件末尾。
	 * @return 写文件成功
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static boolean write(String fileName, byte[] content, boolean append) throws FileNotFoundException, IOException {

		boolean result = false;
		BufferedOutputStream ou = null;
		try {
			ou = new BufferedOutputStream(new FileOutputStream(fileName, append));
			ou.write(content);
			result = true;
		} finally {
			if (ou != null) {
				ou.flush();
				ou.close();
			}
		}
		return result;
		
	}
	
	/**
	 * 创建zip压缩文件
	 * 
	 * @param filenames
	 *            要压缩的文件名称数组
	 * @param targetZipName
	 *            要生成的.zip文件名
	 * @throws IOException
	 */
	public static void zipFile(String[] filenames, String targetZipName) throws FileNotFoundException, IOException {

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetZipName));
		FileInputStream in = null;
		
		byte[] buf = new byte[1024];
		
		try {
			for (int i = 0; i < filenames.length; i++) {
				in = new FileInputStream(filenames[i]);
				
				// 获得文件名
				String name = filenames[i].substring(filenames[i].lastIndexOf(File.separator) + 1);
				out.putNextEntry(new ZipEntry(name));
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
				in = null;
			}
		} finally {
			if (in != null) in.close();
			out.close();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(FileUtil.getBasePath());
		
		File file = FileUtil.getFileByDirAndFile("conf", "ip_province.txt");
		System.out.println(file.getAbsolutePath());

	}
	
}
