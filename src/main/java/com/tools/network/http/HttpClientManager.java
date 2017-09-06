package com.tools.network.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

public final class HttpClientManager {

	public static Registry<ConnectionSocketFactory> vRegistry() {
		return RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
	}

	public static Registry<ConnectionSocketFactory> nRegistry() {
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] {
				new X509TrustManager() {
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
							String paramString) throws CertificateException {
					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
							String paramString) throws CertificateException {
					}

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				}
			}, null);
			return RegistryBuilder.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					})).build();
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static PoolingHttpClientConnectionManager vManager() {
		return new PoolingHttpClientConnectionManager(vRegistry());
	}

	public static PoolingHttpClientConnectionManager nManager() {
		return new PoolingHttpClientConnectionManager(nRegistry());
	}

	public static HttpClientBuilder builder(PoolingHttpClientConnectionManager manager) {
		return HttpClients.custom().setConnectionManager(manager);
	}

	public static HttpClientBuilder vBuilder() {
		return builder(vManager());
	}

	public static HttpClientBuilder nBuilder() {
		return builder(nManager());
	}

	public static PoolingHttpClientConnectionManager demo(PoolingHttpClientConnectionManager manager) {
		manager.setDefaultConnectionConfig(ConnectionConfig.custom().build());
		manager.setDefaultMaxPerRoute(100);
		manager.setDefaultSocketConfig(SocketConfig.custom().build());
		// manager.setConnectionConfig(host, connectionConfig);
		// manager.setMaxPerRoute(route, max);
		// manager.setSocketConfig(host, socketConfig);
		// manager.setMaxTotal(1000);

		return manager;
	}

	public static HttpClientBuilder demo(HttpClientBuilder builder) {
		builder.setUserAgent("").setDefaultSocketConfig(SocketConfig.custom().build())
				.setRetryHandler(new DefaultHttpRequestRetryHandler(1, true))
				.setDefaultCookieStore(new BasicCookieStore()).addInterceptorFirst(new HttpRequestInterceptor() {
					public void process(final HttpRequest request, final HttpContext context) throws HttpException,
							IOException {

					}
				});
		return builder;
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
