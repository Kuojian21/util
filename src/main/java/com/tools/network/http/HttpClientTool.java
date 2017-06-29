package com.tools.network.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.google.common.base.Charsets;
import com.netease.payment.constant.CommonConstant;
import com.netease.payment.constant.LogConstant;
import com.netease.payment.util.http.HttpClientGenerator;
@Repository
public class HttpClientTool {
    
    private static Logger logger = LogConstant.runLog;
    private static HttpClientFlyWeight httpClientGenerator = HttpClientFlyWeight.getInstance(2048, 100);
    private static final ContentType FORM_CONTENT_TYPE_UTF_8 = ContentType.create("application/x-www-form-urlencoded", Charsets.UTF_8);
    
    
    public static String sendURLPostNoParse(String url, NameValuePair []pairs) throws Exception {
    	return sendURLPostNoParse(url, pairs, CommonConstant.URL_CONNECTION_TIME_OUT, CommonConstant.URL_SO_TIME_OUT);
    }
    
    /**
     * 发送POST请求,对返回的内容不进行解析
     * @param url
     * @param pairs
     * @return
     */
    public static String sendURLPostNoParse(String url, NameValuePair []pairs,int connectTimeOut,
            int readTimeOut) throws Exception{
    	
		CloseableHttpClient httpClient = httpClientGenerator.getClient(null);
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(readTimeOut)
					.setConnectionRequestTimeout(connectTimeOut)
					.setConnectTimeout(connectTimeOut)
					.build();
			httpPost.setConfig(requestConfig);
			HttpEntity requestEntity = EntityBuilder.create()
					.setContentType(FORM_CONTENT_TYPE_UTF_8)
					.setParameters(pairs).build();
			httpPost.setEntity(requestEntity);

			response = httpClient.execute(httpPost);
			String xml = EntityUtils.toString(response.getEntity(),Charsets.UTF_8);
			return xml;
		} catch (Exception ex) {
			logger.info("URL POST异常:" + url, ex);
			return null;
		} finally {
			 try {
	                if (response != null) {
	                    //ensure the connection is released back to pool
	                    EntityUtils.consume(response.getEntity());
	                }
	            } catch (IOException e) {
	                logger.warn("close response fail", e);
	            }
		}
    }
    
    
    public static String sendURLGet(String url,int connectTimeOut,int readTimeOut) throws Exception {
		CloseableHttpClient httpClient = httpClientGenerator.getClient(null);
		HttpGet httpPost = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectionRequestTimeout(connectTimeOut)
					.setSocketTimeout(readTimeOut)
					.setConnectTimeout(connectTimeOut)
					.build();
			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httpPost.setConfig(requestConfig);

			response = httpClient.execute(httpPost);
		//	int statusCode = response.getStatusLine().getStatusCode();
			// 要不要只操作200的状态
			String xml = EntityUtils.toString(response.getEntity(),Charsets.UTF_8);
			return xml;
		} catch (Exception ex) {
			logger.info("URL Get异常:" + url, ex);
			return null;
		} finally {
		 try {
                if (response != null) {
                    //ensure the connection is released back to pool
                    EntityUtils.consume(response.getEntity());
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
            }
		}
    }
    
    /**
     * 发送GET请求
     * @param url
     * @return
     */
    public static String sendURLGet(String url) {
		CloseableHttpClient httpClient = httpClientGenerator.getClient(null);
//		System.err.println(httpClientGenerator.stats());
		HttpGet httpPost = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(CommonConstant.URL_SO_TIME_OUT)
					.setConnectionRequestTimeout(CommonConstant.URL_CONNECTION_TIME_OUT)
					.setConnectTimeout(CommonConstant.URL_CONNECTION_TIME_OUT)
					.build();
			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httpPost.setConfig(requestConfig);
			response = httpClient.execute(httpPost);
			String xml = EntityUtils.toString(response.getEntity(),Charsets.UTF_8);
			return xml;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.info("URL Get异常:" + url, ex);
			return null;
		} finally {
			 try {
	                if (response != null) {
	                    //ensure the connection is released back to pool
	                    EntityUtils.consume(response.getEntity());
	                }
	            } catch (IOException e) {
	                logger.warn("close response fail", e);
	            }
		}
    }
    
    /**
     * 发送POST请求[参数为Content]
     * @param url
     * @param content
     * @return
     */
    public static String sendURLPost(String url, String content) {
        return sendURLPost(url, content, CommonConstant.URL_CONNECTION_TIME_OUT/1000, CommonConstant.URL_SO_TIME_OUT/1000);
    }
    public static String sendURLPost(String url, String content, int connectTimeOutSeconds, int readTimeOutSeconds) {
    	return sendURLPost(url, content, connectTimeOutSeconds, readTimeOutSeconds, FORM_CONTENT_TYPE_UTF_8);
    }
    
    /**
     * 发送POST请求[参数为Content]
     * @param url
     * @param content
     * @param connectTimeOutSeconds 连接超时秒数
     * @param readTimeOutSeconds 读超时秒数
     * @return
     */
    public static String sendURLPost(String url, String content, int connectTimeOutSeconds, int readTimeOutSeconds,ContentType contentType) {
		CloseableHttpClient httpClient = httpClientGenerator.getClient(null);
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout( readTimeOutSeconds*1000)
					.setConnectionRequestTimeout(connectTimeOutSeconds*1000)
					.setConnectTimeout(connectTimeOutSeconds*1000)
					.build();
			httpPost.setConfig(requestConfig);
			HttpEntity requestEntity = EntityBuilder.create()
					.setContentType(contentType)
					.setText(content).build();
			httpPost.setEntity(requestEntity);

			response = httpClient.execute(httpPost);
			String xml = EntityUtils.toString(response.getEntity(),Charsets.UTF_8);
			return xml;
		} catch (Exception ex) {
			logger.info("URL POST异常:" + url, ex);
			return null;
		} finally {
			 try {
	                if (response != null) {
	                    //ensure the connection is released back to pool
	                    EntityUtils.consume(response.getEntity());
	                }
	            } catch (IOException e) {
	                logger.warn("close response fail", e);
	            }
		}
    }

	/**
	 *
	 *   @param sendYiPubMsgUrlFinal
	 *   @param postJson
	 *   @param applicationJson
	 *   @return
	 *
	 *   2015年7月12日/下午2:24:30
	 *   mailto:"cuixiang"<cuixiang@corp.netease.com>
	 */
	public static String sendURLPost(String url, String content, ContentType contentType) {
		return sendURLPost(url, content,  CommonConstant.URL_CONNECTION_TIME_OUT/1000, CommonConstant.URL_SO_TIME_OUT/1000, contentType);
	}
	
	/**
	 * 
	 * @author bjpengpeng
	 * @date 2015年9月16日
	 * @description
	 * @param url
	 * @param content
	 * @param pairs
	 * @param contentType
	 * @return
	 */
    public static String sendURLPostFile(String url, File file, Header[] headers, ContentType contentType, Map<String,Object> respHeaders) {
        int connectTimeOutSeconds = CommonConstant.URL_CONNECTION_TIME_OUT;
        int readTimeOutSeconds = CommonConstant.URL_SO_TIME_OUT;
        CloseableHttpClient httpClient = httpClientGenerator.getClient(null);
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(readTimeOutSeconds)
                    .setConnectionRequestTimeout(connectTimeOutSeconds)
                    .setConnectTimeout(connectTimeOutSeconds).build();
            httpPost.setConfig(requestConfig);
            if (null != headers && headers.length != 0) {
                for (Header header : headers) {
                    httpPost.addHeader(header);
                }
            }
            HttpEntity requestEntity = EntityBuilder.create()
                    .setContentType(contentType).setFile(file).build();
            httpPost.setEntity(requestEntity);
            response = httpClient.execute(httpPost);
            String xml = EntityUtils.toString(response.getEntity(),
                    Charsets.UTF_8);
            if (respHeaders != null) {
                Header []rHeaders = response.getAllHeaders();
                if (null != rHeaders) {
                    for (Header h : rHeaders) {
                        respHeaders.put(h.getName(), h.getValue());
                    }
                }
            }
            return xml;
        } catch (Exception ex) {
            logger.info("URL POST异常:" + url, ex);
            return null;
        } finally {
            try {
                if (response != null) {
                    EntityUtils.consume(response.getEntity());
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
            }
        }
    }
	
    /**
     * 
     * @author bjpengpeng
     * @date 2015年9月24日
     * @description 取返回的文件
     * @param url
     * @param headers
     * @param contentType
     * @param respHeaders
     * @param respFile
     */
    public static boolean sendURLPost(String url, Header[] headers, ContentType contentType, Map<String,Object> respHeaders, File respFile) {
        int connectTimeOutSeconds = CommonConstant.URL_CONNECTION_TIME_OUT;
        int readTimeOutSeconds = CommonConstant.URL_SO_TIME_OUT;
        CloseableHttpClient httpClient = httpClientGenerator.getClient(null);
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(readTimeOutSeconds)
                    .setConnectionRequestTimeout(connectTimeOutSeconds)
                    .setConnectTimeout(connectTimeOutSeconds).build();
            httpPost.setConfig(requestConfig);
            if (null != headers && headers.length != 0) {
                for (Header header : headers) {
                    httpPost.addHeader(header);
                }
            }
            HttpEntity requestEntity = EntityBuilder.create()
                    .setContentType(contentType).setText("").build();
            httpPost.setEntity(requestEntity);
            response = httpClient.execute(httpPost);
            if (respHeaders != null) {
                Header []rHeaders = response.getAllHeaders();
                if (null != rHeaders) {
                    for (Header h : rHeaders) {
                        respHeaders.put(h.getName(), h.getValue());
                    }
                }
            }
            InputStream inputStream = response.getEntity().getContent();
            FileOutputStream fileWriter = new FileOutputStream(respFile,true);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buf)) > 0) {
                fileWriter.write(buf, 0, len);
            }
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (Exception ex) {
            logger.info("URL POST异常:" + url, ex);
            return false;
        } finally {
            try {
                if (response != null) {
                    EntityUtils.consume(response.getEntity());
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
            }
        }
    }
    
	public static void main(String[] args) {
		long beginTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			sendURLGet("http://www.taobao.com");
			sendURLGet("http://www.baidu.com");
			sendURLGet("http://www.163.com");
			sendURLGet("http://www.qq.com");
		}
		System.out.println(System.currentTimeMillis()-beginTime);
	}
	
	public static String rawContentPost(String url, String content) {
		CloseableHttpClient httpClient = httpClientGenerator.getClient(null);
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout( CommonConstant.URL_SO_TIME_OUT*1000)
					.setConnectionRequestTimeout(CommonConstant.URL_CONNECTION_TIME_OUT*1000)
					.setConnectTimeout(CommonConstant.URL_CONNECTION_TIME_OUT*1000)
					.build();
			httpPost.setConfig(requestConfig);
			HttpEntity requestEntity = EntityBuilder.create()
					.setContentType(ContentType.parse("text/plain; charset=UTF-8"))
					.setText(content).build();
			httpPost.setEntity(requestEntity);

			response = httpClient.execute(httpPost);
			String xml = EntityUtils.toString(response.getEntity(), Charsets.ISO_8859_1);
			return xml;
		} catch (Exception ex) {
			logger.info("URL POST异常:" + url, ex);
			return null;
		} finally {
			 try {
	                if (response != null) {
	                    //ensure the connection is released back to pool
	                    EntityUtils.consume(response.getEntity());
	                }
	            } catch (IOException e) {
	                logger.warn("close response fail", e);
	            }
		}
    }

}
