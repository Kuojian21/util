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

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public final class HClientManager {

	private static class VerifyHolder {
		private static final HClientManager holder = new HClientManager(true);
	}

	private static class NotVerifyHolder {
		private static final HClientManager holder = new HClientManager(false);
	}

	private PoolingHttpClientConnectionManager connectionManager;

	private HClientManager(boolean verify) {
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

	public static HClientManager getInstance(boolean verify) {
		if (verify) {
			return VerifyHolder.holder;
		}
		return NotVerifyHolder.holder;
	}

	public CloseableHttpClient client(Site site) {
		HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(
				connectionManager);
		
		if (Strings.isNullOrEmpty(site.getUserAgent())) {
			httpClientBuilder.setUserAgent(site.getUserAgent());
		} else {
			httpClientBuilder.setUserAgent("");
		}

		if (site.isUseGzip()) {
			httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {
				public void process(final HttpRequest request, final HttpContext context)
						throws HttpException, IOException {
					if (!request.containsHeader("Accept-Encoding")) {
						request.addHeader("Accept-Encoding", "gzip");
					}
				}
			});
		}

		SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true)
				.setSoTimeout(site.getTimeOut()).build();
		httpClientBuilder.setDefaultSocketConfig(socketConfig);

		httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(site.getRetryTimes(),
				true));

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

		return httpClientBuilder.build();
	}

	public static String stats() {

		PoolingHttpClientConnectionManager[] managers = new PoolingHttpClientConnectionManager[] {
				VerifyHolder.holder.connectionManager, NotVerifyHolder.holder.connectionManager };
		StringBuilder sb = new StringBuilder();
		
		for(PoolingHttpClientConnectionManager manager : managers){
			sb.append("total:").append(manager.getTotalStats().toString()).append("\n");
			for(HttpRoute route : manager.getRoutes()){
				sb.append("\t").append(manager.getStats(route).toString()).append("\n");
			}
		}

		return sb.toString();
	}

}
