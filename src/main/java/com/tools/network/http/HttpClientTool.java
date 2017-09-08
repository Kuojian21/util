package com.tools.network.http;

import java.io.File;
import java.io.IOException;

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
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Charsets;

public class HttpClientTool {

	public static interface ResponseHandle<T> {
		T handle(CloseableHttpResponse response);
	}

	private static final ResponseHandle<String> STRING = new ResponseHandle<String>() {
		@Override
		public String handle(CloseableHttpResponse response) {
			try {
				return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
			} catch (ParseException | IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	};
	private static final PoolingHttpClientConnectionManager manager;
	static {
		manager = HttpClientManager.vManager();
	}

	public static <T> T post(String url, RequestConfig requestConfig, Header[] headers, HttpEntity requestEntity,
			ResponseHandle<T> handle) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			if (headers != null) {
				for (Header header : headers) {
					if (header != null) {
						httpPost.addHeader(header);
					}
				}
			}
			httpPost.setEntity(requestEntity);
			return handle.handle(HttpClients.custom().setConnectionManager(manager).build().execute(httpPost));
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

	public static String post(String url, Header[] headers, NameValuePair[] pairs) {
		HttpEntity requestEntity = EntityBuilder.create()
				.setContentType(ContentType.create("application/x-www-form-urlencoded", Charsets.UTF_8))
				.setParameters(pairs).build();
		return HttpClientTool.post(url, RequestConfig.custom().build(), headers, requestEntity, STRING);
	}

	public static String post(String url, Header[] headers, String content) {
		HttpEntity requestEntity = EntityBuilder.create()
				.setContentType(ContentType.parse("text/plain; charset=UTF-8")).setText(content).build();
		return HttpClientTool.post(url, RequestConfig.custom().build(), headers, requestEntity, STRING);
	}

	public static String post(String url, Header[] headers, File file) {
		HttpEntity requestEntity = EntityBuilder.create().setContentType(ContentType.MULTIPART_FORM_DATA).setFile(file)
				.build();
		return HttpClientTool.post(url, RequestConfig.custom().build(), headers, requestEntity, STRING);
	}

	public static <T> T get(String url, RequestConfig requestConfig, Header[] headers, ResponseHandle<T> handle) {
		CloseableHttpResponse response = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setConfig(requestConfig);
			if (headers != null) {
				for (Header header : headers) {
					if (header != null) {
						httpGet.addHeader(header);
					}
				}
			}
			return handle.handle(HttpClients.custom().setConnectionManager(manager).build().execute(httpGet));
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

	public static String get(String url) {
		return HttpClientTool.get(url, RequestConfig.custom().build(), new Header[] {
			new BasicHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8")
		}, STRING);

	}

	public void demo(RequestConfig requestConfig) {
		RequestConfig.custom().setSocketTimeout(200).setConnectionRequestTimeout(200).setConnectTimeout(200).build();
	}
	
	public static void main(String[] args){
		System.out.println(get("https://www.lmlc.com/m/base_info"));
	}

}
