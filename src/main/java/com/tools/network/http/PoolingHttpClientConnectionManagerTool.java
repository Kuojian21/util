package com.tools.network.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

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
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;


public final class PoolingHttpClientConnectionManagerTool {

	public static Registry<ConnectionSocketFactory> vRegistry() {
		return RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
	}

	public static Registry<ConnectionSocketFactory> nRegistry() throws KeyManagementException {
		try {
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
			return RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https",
							new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
								public boolean verify(String hostname, SSLSession session) {
									return true;
								}
							})).build();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static PoolingHttpClientConnectionManager manager(
			Registry<ConnectionSocketFactory> registry) {
		PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(
				registry);
		manager.setDefaultMaxPerRoute(100);
		return manager;
	}

	public CloseableHttpClient client(Registry<ConnectionSocketFactory> registry,int maxTotal,int defaultMaxPerRoute,String userAgent,List<Cookie> cookies) {
		PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(
				registry);
		manager.setMaxTotal(maxTotal);
		manager.setDefaultMaxPerRoute(defaultMaxPerRoute);

		
		HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(manager);
		
		httpClientBuilder.setUserAgent(userAgent);
		
		httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {
			public void process(final HttpRequest request, final HttpContext context)
					throws HttpException, IOException {
				if (!request.containsHeader("Accept-Encoding")) {
					request.addHeader("Accept-Encoding", "gzip");
				}
			}
		});

		SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true)
				.setSoTimeout(20000).build();
		httpClientBuilder.setDefaultSocketConfig(socketConfig);

		httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(1,
				true));

		CookieStore cookieStore = new BasicCookieStore();
		for (Cookie cookie : cookies) {
			cookieStore.addCookie(cookie);
		}
		httpClientBuilder.setDefaultCookieStore(cookieStore);

		return httpClientBuilder.build();

	}
	
	public static String stats(PoolingHttpClientConnectionManager manager) {
		StringBuilder sb = new StringBuilder();
		sb.append("total:").append(manager.getTotalStats().toString()).append("\n");
		for (HttpRoute route : manager.getRoutes()) {
			sb.append("\t").append(manager.getStats(route).toString()).append("\n");
		}
		return sb.toString();
	}

}
