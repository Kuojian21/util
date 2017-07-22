package com.tools.network.http;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpHost;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Site {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.101 Safari/537.36";

	private String domain;

	private String userAgent = USER_AGENT;

	private boolean useGzip = false;
	
	private int timeOut = 20000;
	
	private int retryTimes = 0;
	
	private Map<String, String> defaultCookies = new LinkedHashMap<String, String>();

	private Table<String, String, String> cookies = HashBasedTable.create();

	private String charset;

	private int sleepTime = 5000;

	

	

	private int connectTimeOut = 30000;

	private static final Set<Integer> DEFAULT_STATUS_CODE_SET = new HashSet<Integer>();

	private Set<Integer> acceptStatCode = DEFAULT_STATUS_CODE_SET;

	private Map<String, String> headers = new HashMap<String, String>();

	private HttpHost httpProxy;

	

	public static Site createDefault() {
		return custom().setRetryTimes(0).setUseGzip(false).setTimeOut(20000)
				.setConnectTimeOut(30000).setUserAgent(USER_AGENT).build();
	}

	/**
	 * @return the connectTimeOut
	 */
	public int getConnectTimeOut() {
		return connectTimeOut;
	}

	/**
	 * @param connectTimeOut
	 *            the connectTimeOut to set
	 */
	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public Map<String, String> getDefaultCookies() {
		return defaultCookies;
	}

	public void setDefaultCookies(Map<String, String> defaultCookies) {
		this.defaultCookies = defaultCookies;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public Set<Integer> getAcceptStatCode() {
		return acceptStatCode;
	}

	public void setAcceptStatCode(Set<Integer> acceptStatCode) {
		this.acceptStatCode = acceptStatCode;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public HttpHost getHttpProxy() {
		return httpProxy;
	}

	public void setHttpProxy(HttpHost httpProxy) {
		this.httpProxy = httpProxy;
	}

	public boolean isUseGzip() {
		return useGzip;
	}

	public void setUseGzip(boolean useGzip) {
		this.useGzip = useGzip;
	}

	/**
	 * Add a cookie with domain {@link #getDomain()}
	 *
	 * @param name
	 * @param value
	 * @return this
	 */
	public Site addCookie(String name, String value) {
		defaultCookies.put(name, value);
		return this;
	}

	/**
	 * Add a cookie with specific domain.
	 *
	 * @param domain
	 * @param name
	 * @param value
	 * @return
	 */
	public Site addCookie(String domain, String name, String value) {
		cookies.put(domain, name, value);
		return this;
	}

	public static SiteBuilder custom() {
		return SiteBuilder.create();
	}

	/**
	 * get cookies
	 *
	 * @return get cookies
	 */
	public Map<String, String> getCookies() {
		return defaultCookies;
	}

	/**
	 * get cookies of all domains
	 *
	 * @return get cookies
	 */
	public Map<String, Map<String, String>> getAllCookies() {
		return cookies.rowMap();
	}

}

class SiteBuilder {

	private Site site;

	private SiteBuilder() {
		site = new Site();
	}

	public static SiteBuilder create() {
		return new SiteBuilder();
	}

	public SiteBuilder addCookie(String domain, String name, String value) {
		site.addCookie(domain, name, value);
		return this;
	}

	public SiteBuilder addCookie(String name, String value) {
		site.addCookie(name, value);
		return this;
	}

	public SiteBuilder setUseGzip(boolean useGzip) {
		site.setUseGzip(useGzip);
		return this;
	}

	public SiteBuilder setHttpProxy(HttpHost httpProxy) {
		site.setHttpProxy(httpProxy);
		return this;
	}

	public SiteBuilder setTimeOut(int timeOut) {
		site.setTimeOut(timeOut);
		return this;
	}

	public SiteBuilder setRetryTimes(int retryTimes) {
		site.setRetryTimes(retryTimes);
		return this;
	}

	public SiteBuilder setUserAgent(String userAgent) {
		site.setUserAgent(userAgent);
		return this;
	}

	public SiteBuilder setDomain(String domain) {
		site.setDomain(domain);
		return this;
	}

	public SiteBuilder setConnectTimeOut(int connectTimeOut) {
		site.setConnectTimeOut(connectTimeOut);
		return this;
	}

	public Site build() {
		return site;
	}

}