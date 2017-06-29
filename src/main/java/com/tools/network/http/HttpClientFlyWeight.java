package com.tools.network.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;

import com.google.common.collect.Maps;

public final class HttpClientFlyWeight {
	
	private static class VerifyHolder {
		private static final HttpClientFlyWeight holder = new HttpClientFlyWeight(true);
	}
	
	private static class NotVerifyHolder{
		private static final HttpClientFlyWeight holder = new HttpClientFlyWeight(false);
	}

	private PoolingHttpClientConnectionManager connectionManager;

	private HttpClientFlyWeight(boolean verify) {
		try {
			Registry<ConnectionSocketFactory> registry = null;

			if (verify) {
				registry = RegistryBuilder.<ConnectionSocketFactory> create()
						.register("http", PlainConnectionSocketFactory.INSTANCE)
						.register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
			} else {
				// 绕过验证
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, new TrustManager[] { new X509TrustManager() {
					@Override
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
							String paramString) throws CertificateException {
					}

					@Override
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
							String paramString) throws CertificateException {
					}

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				} }, null);

				registry = RegistryBuilder
						.<ConnectionSocketFactory> create()
						.register("http", PlainConnectionSocketFactory.INSTANCE)
						.register("https",
								new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
									public boolean verify(String hostname, SSLSession session) {
										return true;
									}
								})).build();

			}
			connectionManager = new PoolingHttpClientConnectionManager(registry);
			connectionManager.setDefaultMaxPerRoute(100);
		} catch (KeyManagementException e) {

		} catch (NoSuchAlgorithmException e) {

		}
	}

	public static HttpClientFlyWeight getInstance(boolean verify) {
		if(verify){
			return VerifyHolder.holder;
		}
		return NotVerifyHolder.holder;
	}

	public CloseableHttpClient getClient(Site site) {
		if (site == null) {
			return generateClient(Site.createDefault());
		}
		return generateClient(site);
	}

	private CloseableHttpClient generateClient(Site site) {
		HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(
				connectionManager);
		if (site != null && site.getUserAgent() != null) {
			httpClientBuilder.setUserAgent(site.getUserAgent());
		} else {
			httpClientBuilder.setUserAgent("");
		}
		if (site == null || site.isUseGzip()) {
			httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {

				public void process(final HttpRequest request, final HttpContext context)
						throws HttpException, IOException {
					if (!request.containsHeader("Accept-Encoding")) {
						request.addHeader("Accept-Encoding", "gzip");
					}

				}
			});
		}
		int soTimeout = CommonConstant.URL_SO_TIME_OUT;
		if (null != site) {
			soTimeout = site.getTimeOut();
		}

		SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true)
				.setSoTimeout(soTimeout).build();
		httpClientBuilder.setDefaultSocketConfig(socketConfig);
		if (site != null) {
			httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(site
					.getRetryTimes(), true));
		}
		if (null != site) {
			generateCookie(httpClientBuilder, site);
		}
		return httpClientBuilder.build();
	}

	private void generateCookie(HttpClientBuilder httpClientBuilder, Site site) {
		CookieStore cookieStore = new BasicCookieStore();
		for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
			BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(),
					cookieEntry.getValue());
			cookie.setDomain(site.getDomain());
			cookieStore.addCookie(cookie);
		}
		for (Map.Entry<String, Map<String, String>> domainEntry : site.getAllCookies().entrySet()) {
			for (Map.Entry<String, String> cookieEntry : domainEntry.getValue().entrySet()) {
				BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(),
						cookieEntry.getValue());
				cookie.setDomain(domainEntry.getKey());
				cookieStore.addCookie(cookie);
			}
		}
		httpClientBuilder.setDefaultCookieStore(cookieStore);
	}

	/**
	 * 
	 * 提供连接池的统计信息 注意： 必须首先存在连接池的实例
	 *
	 * @return
	 *
	 *         2015年7月14日/下午9:10:42 mailto:"cuixiang"<cuixiang@corp.netease.com>
	 */
	public static String stats() {
		StringBuilder sb = new StringBuilder();
		sb.append("total:").append(instance.connectionManager.getTotalStats().toString())
				.append("---[");
		for (HttpRoute route : instance.connectionManager.getRoutes()) {
			sb.append(instance.connectionManager.getStats(route).toString()).append("||");
		}
		sb.append("]");
		return sb.toString();
	}

}
