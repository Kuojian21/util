package com.tools.network.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Charsets;
import com.tools.io.IOTool;

public class CloseableHttpClientTool {

	
	public static String post(CloseableHttpClient client, String url, NameValuePair[] pairs,
			ContentType contentType, int connectTimeOut, int readTimeOut) {

		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		try {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeOut)
					.setConnectionRequestTimeout(connectTimeOut).setConnectTimeout(connectTimeOut)
					.build();
			httpPost.setConfig(requestConfig);
			HttpEntity requestEntity = EntityBuilder
					.create()
					.setContentType(
							ContentType.create("application/x-www-form-urlencoded", Charsets.UTF_8))
					.setParameters(pairs).build();
			httpPost.setEntity(requestEntity);

			response = client.execute(httpPost);
			return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String get(CloseableHttpClient client, String url, int connectTimeOut,
			int readTimeOut) {

		CloseableHttpResponse response = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(connectTimeOut).setSocketTimeout(readTimeOut)
					.setConnectTimeout(connectTimeOut).build();
			httpGet.setConfig(requestConfig);
			httpGet.setHeader("Content-NODE_TYPE", "application/x-www-form-urlencoded;charset=UTF-8");
			response = client.execute(httpGet);
			return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public static String post(CloseableHttpClient client,String url, String content, int connectTimeOut,
			int readTimeOut) {
		
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(connectTimeOut)
					.setConnectionRequestTimeout(readTimeOut)
					.setConnectTimeout(connectTimeOut).build();
			httpPost.setConfig(requestConfig);
			HttpEntity requestEntity = EntityBuilder.create().setContentType(ContentType.parse("text/plain; charset=UTF-8"))
					.setText(content).build();
			httpPost.setEntity(requestEntity);

			response = client.execute(httpPost);
			return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String post(CloseableHttpClient client,String url, File file, Header[] headers,int connectTimeOut,
			int readTimeOut) {		
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(readTimeOut)
					.setConnectionRequestTimeout(connectTimeOut)
					.setConnectTimeout(connectTimeOut).build();
			httpPost.setConfig(requestConfig);
			if (null != headers && headers.length != 0) {
				for (Header header : headers) {
					httpPost.addHeader(header);
				}
			}
			HttpEntity requestEntity = EntityBuilder.create().setContentType(ContentType.MULTIPART_FORM_DATA)
					.setFile(file).build();
			httpPost.setEntity(requestEntity);
			response = client.execute(httpPost);
			return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void post(CloseableHttpClient client,String url, File file, Header[] headers,int connectTimeOut,
			int readTimeOut,OutputStream out) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(readTimeOut)
					.setConnectionRequestTimeout(connectTimeOut)
					.setConnectTimeout(connectTimeOut).build();
			httpPost.setConfig(requestConfig);
			if (null != headers && headers.length != 0) {
				for (Header header : headers) {
					httpPost.addHeader(header);
				}
			}
			HttpEntity requestEntity = EntityBuilder.create().setContentType(ContentType.parse("text/plain; charset=UTF-8"))
					.setText("").build();
			httpPost.setEntity(requestEntity);
			response = client.execute(httpPost);
			InputStream inputStream = response.getEntity().getContent();
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			IOTool.close(out);
		}
	}


}
