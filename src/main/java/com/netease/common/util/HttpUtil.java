package com.netease.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.remoting.RemoteInvocationFailureException;

import com.netease.common.exception.AppException;

/**
 * 负责发起网络连接的util类<br>
 * 如果需要建立自定义套接字的安全连接，请使用带sslContext参数的方法，否则无需特殊指定<br/>
 * 
 * @author 开发支持中心
 */
public class HttpUtil{
	public static final int DEFAULT_CONNECT_TIME_OUT = 30000;
	public static final int DEFAULT_READ_TIME_OUT = 30000;
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final int DEFAULT_RETRY_TIME = 1;// 打开URL失败默认重新连接的次数
	
	private static String getErrorStack(Exception e)
	{
		String errorStack = null;
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		e.printStackTrace(ps);
		errorStack = new String(out.toByteArray());
		
		return errorStack;
	}
	
	static Pattern pattern = Pattern.compile("at [^\\)]+\\)");
	private static String locateInvokerStack(String errorStack)
	{
		String result = null;
		
		errorStack = errorStack.substring(errorStack.lastIndexOf(".HttpUtil."));
		Matcher matcher = pattern.matcher(errorStack);
		if (matcher.find())
		{
			result = matcher.group();
		}
		return result;
	}

	public static String sendRequest(String url, int connectTimeout,
			int readTimeout, String charset, boolean returnSingle)
			throws RemoteInvocationFailureException {
		BufferedReader in = null;
		HttpURLConnection conn = null;
		try {
			if (StringUtils.isBlank(charset)) {
				charset = DEFAULT_CHARSET;
			}
			conn = getURLConnection(url, connectTimeout, readTimeout);
			in = new BufferedReader(new InputStreamReader(connect(conn),
					charset));
			String result = getReturnResult(in, returnSingle);
			if (StringUtils.isBlank(result)) {
				throw new RemoteInvocationFailureException("网络异常，" + url
						+ "无法联通", null);
			}
			return result;
		} catch (IOException e) {
			// logger.error("", e);
			LoggerUtil.alarmInfoStrategy10m10("[通用模块][紧急][网络IO异常 : " + e.getMessage() + ", 调用堆栈 : " + locateInvokerStack(getErrorStack(e)));
			throw new RemoteInvocationFailureException("网络IO异常[" + url + "]", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}

				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				// logger.error("", e);
			}
		}
	}

	public static String sendRequest(String url, boolean returnSingle)
			throws RemoteInvocationFailureException {
		return sendRequest(url, DEFAULT_CONNECT_TIME_OUT,
				DEFAULT_READ_TIME_OUT, DEFAULT_CHARSET, returnSingle);
	}

	/**
	 * 建立远程连接
	 * 
	 * @param urlStr
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 */
	private static InputStream connect(HttpURLConnection httpConn) {
		String urlStr = httpConn.getURL().toString();
		try {
			if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RemoteInvocationFailureException("远程访问" + urlStr
						+ "出错，返回结果为：" + httpConn.getResponseCode(), null);
			}
			return httpConn.getInputStream();
		} catch (IOException e) {
			// logger.error("", e);
			LoggerUtil.alarmInfoStrategy10m10("[通用模块][紧急][网络IO异常 : " + e.getMessage() + ", 调用堆栈 : " + locateInvokerStack(getErrorStack(e)));
			throw new RemoteInvocationFailureException(
					"网络IO异常[" + urlStr + "]", e);
		}
	}

	/**
	 * 构造URLConnnection
	 * 
	 * @param urlStr
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 * @throws RemoteInvocationFailureException
	 */
	private static HttpURLConnection getURLConnection(String urlStr,
			int connectTimeout, int readTimeout)
			throws RemoteInvocationFailureException {
		// logger.debug("请求URL:" + urlStr);
		try {
			URL remoteUrl = new URL(urlStr);
			HttpURLConnection httpConn = (HttpURLConnection) remoteUrl
					.openConnection();
			httpConn.setConnectTimeout(connectTimeout);
			httpConn.setReadTimeout(readTimeout);
			return httpConn;
		} catch (MalformedURLException e) {
			// logger.error("", e);
			throw new RemoteInvocationFailureException(
					"远程访问异常[" + urlStr + "]", e);
		} catch (IOException e) {
			// logger.error("", e);
			throw new RemoteInvocationFailureException(
					"网络IO异常[" + urlStr + "]", e);
		}
	}

	private static String getReturnResult(BufferedReader in,
			boolean returnSingleLine) throws IOException {
		if (returnSingleLine) {
			return in.readLine();
		} else {
			StringBuffer sb = new StringBuffer();
			String result = "";
			while ((result = in.readLine()) != null) {
				sb.append(StringUtils.trimToEmpty(result));
			}
			return sb.toString();
		}
	}
	
	public static String sendPostRequest(String url, String content, String charset)
	{
		return sendPostRequest(url, content, charset, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT);
	}

	public static String sendPostRequest(String url, String content, String charset, int connectTimeout, int readTimeout)
			throws RemoteInvocationFailureException
	{
		return sendPostRequest(url, content, charset, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT, false);
	}

	public static String sendPostRequest(String url, String content, String charset, int connectTimeout, int readTimeout,
			boolean needCompress) throws RemoteInvocationFailureException
	{
		BufferedReader in = null;
		HttpURLConnection httpConn = null;
		try
		{
			httpConn = getURLConnection(url, connectTimeout, readTimeout);
			if (StringUtils.isBlank(charset))
			{
				charset = DEFAULT_CHARSET;
			}
			//			logger.debug("请求发送地址:" + url);
			//logger.debug("参数:" + content);
			InputStream stream = postConnect(httpConn, content, charset, needCompress);

			in = new BufferedReader(new InputStreamReader(stream, charset));
			String result = getReturnResult(in, false);
			//logger.debug("请求返回结果:" + result);
			if (StringUtils.isBlank(result))
			{
				throw new RemoteInvocationFailureException("网络异常，" + url + "无法联通", null);
			}
			return result;
		}
		catch (IOException e)
		{
			//logger.error("", e);
			throw new RemoteInvocationFailureException("网络IO异常[" + url + "]", e);
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}

				if (httpConn != null)
				{
					httpConn.disconnect();
				}
			}
			catch (IOException e)
			{
				//logger.error("", e);
			}
		}
	}

	public static String sendPostRequest(String url, String content, String inCharset, String outCharset, int connectTimeout,
			int readTimeout, boolean needCompress) throws RemoteInvocationFailureException
	{
		BufferedReader in = null;
		HttpURLConnection httpConn = null;
		try
		{
			httpConn = getURLConnection(url, connectTimeout, readTimeout);
			if (StringUtils.isBlank(inCharset))
			{
				inCharset = DEFAULT_CHARSET;
			}
			//			logger.debug("请求发送地址:" + url);
			//logger.debug("参数:" + content);
			InputStream stream = postConnect(httpConn, content, inCharset, needCompress);

			in = new BufferedReader(new InputStreamReader(stream, outCharset));
			String result = getReturnResult(in, false);
			//logger.debug("请求返回结果:" + result);
			if (StringUtils.isBlank(result))
			{
				throw new RemoteInvocationFailureException("网络异常，" + url + "无法联通", null);
			}
			return result;
		}
		catch (IOException e)
		{
			//logger.error("", e);
			throw new RemoteInvocationFailureException("网络IO异常[" + url + "]", e);
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}

				if (httpConn != null)
				{
					httpConn.disconnect();
				}
			}
			catch (IOException e)
			{
				//logger.error("", e);
			}
		}
	}

	public static String sendPostRequest(String url, String content, String charset, boolean needCompress)
			throws RemoteInvocationFailureException
	{
		return sendPostRequest(url, content, charset, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT, needCompress);
	}

	private static InputStream postConnect(HttpURLConnection httpConn, String content, String charset, boolean needCompress)
	{
		String urlStr = httpConn.getURL().toString();
		try
		{
			if (StringUtils.isBlank(charset))
			{
				charset = DEFAULT_CHARSET;
			}
			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,
			// 默认情况下是false;
			httpConn.setDoOutput(true);
			// Post 请求不能使用缓存
			httpConn.setUseCaches(false);
			// 设定请求的方法为"POST"，默认是GET
			httpConn.setRequestMethod("POST");
			if (needCompress)
			{
				sendCompressRequest(content, charset, httpConn);
			}
			else
			{
				sendNoCompressRequest(content, charset, httpConn);
			}
			// 接收数据
			if (needCompress)
			{
				return new GZIPInputStream(httpConn.getInputStream());
			}
			return httpConn.getInputStream();
		}
		catch (MalformedURLException e)
		{
			//logger.error("", e);
			throw new RemoteInvocationFailureException("远程访问异常[" + urlStr + "]", e);
		}
		catch (IOException e)
		{
			//logger.error("", e);
			throw new RemoteInvocationFailureException("网络IO异常[" + urlStr + "]", e);
		}
	}

	

	/**
	 * 采用缺少的连接延迟、读延迟发送get请求。
	 * 
	 * @param url Get请求URL地址
	 * @return 请求返回字节数组
	 * @throws AppException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static byte[] sendGetRequest(String url) throws MalformedURLException, IOException, AppException
	{
		return sendGetRequest(url, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT, null);
	}

	/**
	 * 采用缺少的连接延迟、读延迟发送get请求。<br>
	 * 可以指定truststore，请自行创建sslContext
	 * 
	 * @param url
	 * @param sslContext
	 * @return 请求返回字节数组
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws AppException
	 */
	public static byte[] sendGetRequest(String url, SSLContext sslContext) throws MalformedURLException, IOException,
			AppException
	{
		return sendGetRequest(url, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT, sslContext);
	}

	/**
	 * 
	 * @param url
	 * @param connectTimeout
	 *            连接延迟
	 * @param readTimeout
	 *            读延迟
	 * @param sslContext
	 *            自定义安全套接字
	 * @return 请求返回字节数组
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws AppException
	 */
	public static byte[] sendGetRequest(String url, int connectTimeout, int readTimeout, SSLContext sslContext)
			throws MalformedURLException, IOException, AppException
	{
		InputStream is = null;
		HttpURLConnection conn = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try
		{
			if (null == sslContext)
			{
				conn = openConnection(url, connectTimeout, readTimeout);
			}
			else
			{
				conn = openConnection(url, connectTimeout, readTimeout, sslContext);
			}
			is = connectByGet(conn);

			int ch;
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(baos);
			while ((ch = bis.read()) != -1)
			{
				bos.write(ch);
			}
			bos.flush();
			bis.close();
			return baos.toByteArray();
		}
		finally
		{
			try
			{
				if (bis != null)
				{
					bis.close();
				}
				if (is != null)
				{
					is.close();
				}
				if (conn != null)
				{
					conn.disconnect();
				}
			}
			catch (IOException e)
			{
				throw new IOException("[连接关闭异常]", e);
			}
		}
	}

	/**
	 * post方式调用接口。
	 * 
	 * @param url
	 *            不带参数的url。
	 * @param content
	 *            格式为“param1=value1&param2=value2&...”的内容。
	 * @param charset
	 *            编码采用的字符集
	 * @return 请求返回字节数组
	 * @throws IOException
	 * @throws MalformedURLException
	 */

	public static byte[] sendPostRequestForBytes(String url, String content, String charset) throws MalformedURLException,
			IOException
	{
		return sendPostRequest(url, content, charset, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT, false, null);
	}

	/**
	 * post方式调用接口。
	 * 
	 * @param url
	 *            不带参数的url。
	 * @param content
	 *            格式为“param1=value1&param2=value2&...”的内容。
	 * @param charset
	 *            编码采用的字符集
	 * @param sslContext
	 *            自定义安全套接字
	 * @return 请求返回字节数组
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static byte[] sendPostRequest(String url, String content, String charset, SSLContext sslContext)
			throws MalformedURLException, IOException
	{
		return sendPostRequest(url, content, charset, DEFAULT_CONNECT_TIME_OUT, DEFAULT_READ_TIME_OUT, false,
				sslContext);
	}

	/**
	 * post方式发送请求。
	 * 
	 * @param url
	 * @param content
	 * @param charset
	 * @param connectTimeout
	 * @param readTimeout
	 * @param needCompress
	 * @param sslContext
	 * @return 请求返回字节数组
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static byte[] sendPostRequest(String url, String content, String charset, int connectTimeout,
			int readTimeout, boolean needCompress, SSLContext sslContext) throws MalformedURLException, IOException
	{
		InputStream is = null;
		// BufferedReader in = null;
		HttpURLConnection httpConn = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		charset = StringUtils.isBlank(charset) ? DEFAULT_CHARSET : charset;
		try
		{
			// 建立连接。
			if (null == sslContext)
			{
				httpConn = openConnection(url, connectTimeout, readTimeout);
			}
			else
			{
				httpConn = openConnection(url, connectTimeout, readTimeout, sslContext);
			}
			is = connectByPost(httpConn, content, charset, needCompress);
			// int size = stream.available();
			// int size = httpConn.getContentLength();
			int ch;
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(baos);
			while ((ch = bis.read()) != -1)
			{
				bos.write(ch);
			}
			bos.flush();
			bis.close();
			return baos.toByteArray();
		}
		finally
		{
			try
			{
				if (bis != null)
				{
					bis.close();
				}
				if (httpConn != null)
				{
					httpConn.disconnect();
				}
			}
			catch (Exception e)
			{
				throw new IOException("[连接关闭异常]", e);
			}
		}
	}

	/**
	 * 按照压缩格式发送信息。
	 * 
	 * @param content
	 * @param charset
	 * @param httpConn
	 * @throws IOException
	 */

	private static void sendCompressRequest(String content, String charset, HttpURLConnection httpConn)
			throws IOException
	{
		GZIPOutputStream out = null;
		try
		{
			httpConn.setRequestProperty("Content-Type", "application/x-gzip");
			httpConn.setRequestProperty("Accept", "application/x-gzip");
			out = new GZIPOutputStream(httpConn.getOutputStream());
			out.write(content.getBytes(charset));
			out.flush();
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					throw new IOException("[连接关闭异常][" + httpConn.getURL().toString() + "]", e);
				}
			}
		}
	}

	/**
	 * 发送无需压缩的请求信息。
	 * 
	 * @param content
	 * @param charset
	 * @param httpConn
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private static void sendNoCompressRequest(String content, String charset, HttpURLConnection httpConn)
			throws UnsupportedEncodingException, IOException
	{
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(new OutputStreamWriter(httpConn.getOutputStream(), charset));
			out.write(content);
			out.flush();
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
		}
	}

	/**
	 * 建立get方式连接。
	 * 
	 * @param urlStr
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	private static InputStream connectByGet(HttpURLConnection httpConn) throws IOException, AppException
	{
		String urlStr = httpConn.getURL().toString();
		if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK)
		{
			throw new AppException("远程访问" + urlStr + "出错，返回结果为：" + httpConn.getResponseCode());
		}
		return httpConn.getInputStream();
	}

	/**
	 * 建立 post连接
	 * 
	 * @param httpConn
	 * @param content
	 * @param charset
	 * @param needCompress
	 * @return
	 * @throws IOException
	 */
	private static InputStream connectByPost(HttpURLConnection httpConn, String content, String charset,
			boolean needCompress) throws IOException
	{
		charset = StringUtils.isBlank(charset) ? DEFAULT_CHARSET : charset;
		// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,
		// 默认情况下是false;
		httpConn.setDoOutput(true);
		// Post 请求不能使用缓存
		httpConn.setUseCaches(false);
		// 设定请求的方法为"POST"，默认是GET
		httpConn.setRequestMethod("POST");
		// 发送请求并接受响应信息
		if (needCompress)
		{
			sendCompressRequest(content, charset, httpConn);
			return new GZIPInputStream(httpConn.getInputStream());
		}
		else
		{
			sendNoCompressRequest(content, charset, httpConn);
			return httpConn.getInputStream();
		}
	}

	/**
	 * 建立连接
	 * 
	 * @param url
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 * @throws MalformedURLException
	 *             If the string specifies an unknown protocol.
	 * @throws IOException
	 *             if an I/O exception occurs
	 */
	private static HttpURLConnection openConnection(String url, int connectTimeout, int readTimeout) throws IOException
	{
		URL remoteUrl = new URL(url);
		HttpURLConnection httpConn = (HttpURLConnection) remoteUrl.openConnection();
		httpConn.setConnectTimeout(connectTimeout);
		httpConn.setReadTimeout(readTimeout);
		return httpConn;
	}

	/**
	 * 建立https连接
	 * 
	 * @param url
	 * @param connectTimeout
	 * @param readTimeout
	 * @param sslContext
	 *            自定义套接字
	 * @return
	 * @throws IOException
	 */
	private static HttpsURLConnection openConnection(String url, int connectTimeout, int readTimeout,
			SSLContext sslContext) throws IOException
	{
		HostnameVerifier hostnameVerifier = new HostnameVerifier()
		{
			public boolean verify(String arg0, SSLSession arg1)
			{
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		URL remoteUrl = new URL(url);
		HttpsURLConnection httpsConn = (HttpsURLConnection) remoteUrl.openConnection();
		httpsConn.setConnectTimeout(connectTimeout);
		httpsConn.setReadTimeout(readTimeout);
		if (null != sslContext)
		{
			httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
		}
		return httpsConn;
	}

	public static void main(String[] args) throws MalformedURLException, IOException, AppException
	{
		System.out
				.println(new String(sendGetRequest("http://smsknl.163.com:8089/servlet/Phone2Area?phone=13581505229")));
		System.out.println(new String(
				sendGetRequest("https://epay.163.com/service/login?userName=urstest_liao9@163.com&password=asdfgh")));
	}

	/*
	 * private static String read( BufferedReader in, boolean readOneLine)
	 * throws IOException { if (readOneLine) { //读一行信息 return in.readLine(); }
	 * else { //读取全部信息。 StringBuffer sb = new StringBuffer(); String result =
	 * ""; while ((result = in.readLine()) != null) {
	 * sb.append(StringUtils.trimToEmpty(result)); } return sb.toString(); } }
	 */

	/**
	 * 本方法用于调用远程的接口
	 * 
	 * @param String
	 *            接口的url
	 * @return 返回调用接收Url后的返回值
	 */
	/*
	 * public static String openUrl(String strUrl) throws NetConnectionException
	 * { String result = ""; for(int i =0;i<DEFAULT_RETRY_TIME;i++) { try {
	 * result =
	 * openUrlBody(strUrl,DEFAULT_CONNECT_TIMEOUT,DEFAULT_READ_TIMEOUT); break;
	 * } catch(NetConnectionException e) { if(i >= DEFAULT_RETRY_TIME -1) {
	 * log.fatal("connect to url[" + strUrl + "] failed", e); throw new
	 * NetConnectionException("Connect to url[" + strUrl + "] error"); } else {
	 * log.warn("connect to url[" + strUrl + "] failed " +i + " times", e); } }
	 * } return result; }
	 */
	/**
	 * 本方法用于调用远程的接口
	 * 
	 * @param String
	 *            接口的url
	 * @return 返回调用接收Url后的返回值
	 */
	/*
	 * public static String openUrl(String strUrl,int retryTimes,int
	 * connectTimeOut,int readTimeOut) throws NetConnectionException { String
	 * result = ""; for(int i =0;i<retryTimes;i++) { try { result =
	 * openUrlBody(strUrl,connectTimeOut,readTimeOut); break; }
	 * catch(NetConnectionException e) { if(i >= retryTimes -1) {
	 * log.fatal("connect to url[" + strUrl + "] failed", e); throw new
	 * NetConnectionException("Connect to url[" + strUrl + "] error"); } else {
	 * log.warn("connect to url[" + strUrl + "] failed " +i + " times", e); } }
	 * } return result; }
	 */

	/**
	 * 打开URL获取内容 如果错误则抛出异常
	 */
	/*
	 * private static String openUrlBody(String strUrl,int connectionTime, int
	 * readTimeout) throws NetConnectionException { String result = null;
	 * InputStream stream = null;
	 * 
	 * try { URL webUrl = new URL(strUrl); HttpURLConnection httpConn =
	 * (HttpURLConnection) webUrl.openConnection();
	 * httpConn.setConnectTimeout(connectionTime);
	 * httpConn.setReadTimeout(readTimeout); if (httpConn.getResponseCode() !=
	 * HttpURLConnection.HTTP_OK) { log.warn(strUrl + "|ResponseCode=" +
	 * httpConn.getResponseCode()); throw new
	 * NetConnectionException("Connect to url[" + strUrl +
	 * "] error, response code is " + httpConn.getResponseCode()); }
	 * 
	 * stream = httpConn.getInputStream(); byte buf[] = new byte[1000]; int i,
	 * rtn = 0; for (i = 0; i < 1000; i++) { rtn = stream.read(); if (rtn < 0) {
	 * break; } buf[i] = (byte) rtn; } String output = new String(buf, 0, i,
	 * "utf-8"); result = output; return result; } catch (Exception e) { throw
	 * new NetConnectionException("Connect to url[" + strUrl + "] error"); }
	 * finally { if (stream != null) { try { stream.close(); } catch
	 * (IOException e1) { LoggerUtil.error(e1, HttpUtil.class.getName(),
	 * "openUrl(String strUrl)"); } } } }
	 */

	/**
	 * 
	 * @param strUrl
	 * @param connectionTime
	 * @param readTimeout
	 * @return
	 * @throws NetConnectionException
	 */
	/*
	 * public static String openUrl(String strUrl, int connectionTime, int
	 * readTimeout) throws NetConnectionException { BufferedReader in = null;
	 * try { URL webUrl = new URL(strUrl); HttpURLConnection httpConn =
	 * (HttpURLConnection) webUrl.openConnection();
	 * httpConn.setConnectTimeout(connectionTime);
	 * httpConn.setReadTimeout(readTimeout);
	 * 
	 * if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
	 * LoggerUtil.warn(strUrl + "|ResponseCode=" + httpConn.getResponseCode(),
	 * HttpUtil.class.getName(), "openUrl"); throw new NetConnectionException();
	 * } in = new BufferedReader(new
	 * InputStreamReader(httpConn.getInputStream())); String line = null; String
	 * result = ""; while ((line = in.readLine()) != null) { result = result +
	 * line; } return result; } catch (Exception e) { LoggerUtil.fatal(e,
	 * HttpUtil.class.getName(),
	 * "openUrl(String strUrl,int connectionTime,int readTimeout )", strUrl);
	 * throw new NetConnectionException(e); } finally { if (in != null) { try {
	 * in.close(); } catch (IOException e1) { LoggerUtil.error(e1,
	 * HttpUtil.class.getName(),
	 * "openUrl(String strUrl,int connectionTime,int readTimeout )"); } } } }
	 */

	/**
	 * 只返回第一行的方法
	 * 
	 * @param strUrl
	 * @param connectionTime
	 * @param readTimeout
	 * @return
	 * @throws NetConnectionException
	 */
	/*
	 * public static String openUrlReturnFirstLine(String strUrl, int
	 * connectionTime, int readTimeout) throws NetConnectionException {
	 * BufferedReader in = null; try { URL webUrl = new URL(strUrl);
	 * HttpURLConnection httpConn = (HttpURLConnection) webUrl.openConnection();
	 * httpConn.setConnectTimeout(connectionTime);
	 * httpConn.setReadTimeout(readTimeout);
	 * 
	 * if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
	 * LoggerUtil.warn(strUrl + "|ResponseCode=" + httpConn.getResponseCode(),
	 * HttpUtil.class.getName(), "openUrl"); throw new NetConnectionException();
	 * } in = new BufferedReader(new
	 * InputStreamReader(httpConn.getInputStream())); String line = null; String
	 * result = ""; if ((line = in.readLine()) != null) { result = line; }
	 * return result; } catch (Exception e) { LoggerUtil.fatal(e,
	 * HttpUtil.class.getName(),
	 * "openUrl(String strUrl,int connectionTime,int readTimeout )", strUrl);
	 * throw new NetConnectionException(e); } finally { if (in != null) { try {
	 * in.close(); } catch (IOException e1) { LoggerUtil.error(e1,
	 * HttpUtil.class.getName(),
	 * "openUrl(String strUrl,int connectionTime,int readTimeout )"); } } } }
	 */

	/**
	 * 设置让浏览器弹出下载对话框的Header.
	 * 
	 * @param fileName
	 *            下载后的文件名.
	 */
	public static void setDownloadableHeader(HttpServletResponse response, String fileName)
	{
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
	}

	/**
	 * 设置LastModified Header.
	 */
	public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate)
	{
		response.setDateHeader("Last-Modified", lastModifiedDate);
	}

	/**
	 * 设置Etag Header.
	 */
	public static void setEtagHeader(HttpServletResponse response, String etag)
	{
		response.setHeader("ETag", etag);
	}

	/**
	 * 设置过期时间 Header.
	 */
	public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds)
	{
		// Http 1.0 header
		response.setDateHeader("Expires", System.currentTimeMillis() + expiresSeconds * 1000);
		// Http 1.1 header
		response.setHeader("Cache-Control", "max-age=" + expiresSeconds);
	}

	/**
	 * 设置无缓存Header.
	 */
	public static void setNoCacheHeader(HttpServletResponse response)
	{
		// Http 1.0 header
		response.setDateHeader("Expires", 0);
		// Http 1.1 header
		response.setHeader("Cache-Control", "no-cache");
	}

	/**
	 * 检查浏览器客户端是否支持gzip编码.
	 */
	public static boolean checkAccetptGzip(HttpServletRequest request)
	{
		// Http1.1 header
		String acceptEncoding = request.getHeader("Accept-Encoding");

		return StringUtils.contains(acceptEncoding, "gzip");
	}

	/**
	 * 设置Gzip Header并返回GZIPOutputStream.
	 */
	/*
	 * public static OutputStream buildGzipOutputStream(HttpServletResponse
	 * response) throws IOException { response.setHeader("Content-Encoding",
	 * "gzip"); return new GZIPOutputStream(response.getOutputStream()); }
	 */

	/**
	 * 根据浏览器If-Modified-Since Header, 计算文件是否已修改.
	 * 
	 * 如果无修改, checkIfModify返回false ,设置304 not modify status.
	 */
	/*
	 * public static boolean checkIfModifiedSince( HttpServletRequest request,
	 * HttpServletResponse response, long lastModified) { long ifModifiedSince =
	 * request.getDateHeader("If-Modified-Since"); if ((ifModifiedSince != -1)
	 * && (lastModified < ifModifiedSince + 1000)) {
	 * response.setStatus(HttpServletResponse.SC_NOT_MODIFIED); return false; }
	 * return true; }
	 */

	/**
	 * 根据浏览器 If-None-Match Header,计算Etag是否无效.
	 * 
	 * 如果Etag有效,checkIfNoneMatch返回false, 设置304 not modify status.
	 */
	/*
	 * public static boolean checkIfNoneMatchEtag(HttpServletRequest request,
	 * HttpServletResponse response, String etag) { String headerValue =
	 * request.getHeader("If-None-Match"); if (headerValue != null) { boolean
	 * conditionSatisfied = false; if (!headerValue.equals("*")) {
	 * StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");
	 * 
	 * while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) { String
	 * currentToken = commaTokenizer.nextToken(); if
	 * (currentToken.trim().equals(etag)) { conditionSatisfied = true; } } }
	 * else { conditionSatisfied = true; }
	 * 
	 * if (conditionSatisfied) {
	 * response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
	 * response.setHeader("ETag", etag); return false; } } return true; }
	 */
}
