package com.tools.secret;

import java.io.UnsupportedEncodingException;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * 编码、解码工具类，主要包括以下编解码方式：<br/>
 * hex、base36、base64、url、html、xml等<br />
 * 
 * @author 开发支持中心。
 * 
 */
public class CodecUtil
{

	private static final String DEFAULT_URL_ENCODING = "UTF-8";
	
	//用于Base36
	private static final char[]	 ALPHABET = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
		'X', 'Y', 'Z'		   };
	static final int[]	  INVERTED_ALPHABET;
	//用于Base36。长度为68.
	static final String	 INIT_STR  = "00000000000000000000000000000000000000000000000000000000000000000000";
	static {
		INVERTED_ALPHABET = new int[128];
		for (int i = 0; i < 128; i++) {
			INVERTED_ALPHABET[i] = -1;
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			INVERTED_ALPHABET[i] = (i - 'A' + 10);
		}
		for (int i = '0'; i <= '9'; i++) {
			INVERTED_ALPHABET[i] = (i - '0');
		}
	}
	static final BigInteger MODULUS   = new BigInteger("36");
		
	/**
	 * Hex编码。一个字节对应2个字符，返回的字符串长度=2*入口参数的长度。
	 * 
	 * @param data
	 *           十六进制字节数组。
	 * @return
	 *           编码后的字符串。如果hexData为null或长度为0，返回null。
	 */
	public static String hexEncode(byte[] data)
	{
		if ( data == null || data.length == 0 )
			return null;
		
		return Hex.encodeHexString(data);
	}

	/**
	 * Hex解码.
	 * 
	 * @param str
	 *           待解码的字符串。
	 * @return 解码后的十六进制字节数组。如果str为null或长度为0，返回null。           
	 * @throws DecoderException 
	 */
	public static byte[] hexDecode(String str) throws DecoderException
	{
		byte[] result = null ;
		if ( str == null || str.length() == 0 )
			return result;
		
		//str是否是十六进制字符串
		
		//try {
			return Hex.decodeHex(str.toCharArray());
		//} catch (DecoderException e) {
		//	LogUtil.error("字符串\""+str+"\"hex解码异常！",e);
		//	throw new IllegalArgumentException("字符串\""+str+"\"hex解码异常！",e);
		//}
		//return result;
	
	}

	/**
	 * Base64编码.
	 * 
	 * @param binaryData
	 *           待编码的字节数组。
	 * @return 编码后的字符串。如果input为null或长度为0，返回null。
	 */
	public static String base64Encode(byte[] binaryData)
	{
		if ( binaryData == null || binaryData.length == 0 )
			return null;
		
		return Base64.encodeBase64String(binaryData);
	}

	/**
	 * URL Base64编码, URL安全(对URL不支持的字符如+,/,=转为其他字符, 见RFC3548).
	 * 
	 * @param binaryData
	 *          binary data to encode
	 * @return
	 *         String containing Base64 characters。如果binaryData为null或长度为0，返回null.
	 */
	public static String base64UrlEncode(byte[] binaryData)
	{
		if ( binaryData == null || binaryData.length == 0 )
			return null;
		
		return Base64.encodeBase64URLSafeString(binaryData);
	}

	/**
	 * Base64解码.
	 * 
	 * @param base64String 
	 *           待解码的字符串。
	 * @return 
	 *           Array containing decoded data.如果base64String为null或长度为0，返回null
	 */
	public static byte[] base64Decode(String base64String)
	{
		if ( base64String  == null  || base64String.length() == 0 )
			return null;
		
		return Base64.decodeBase64(base64String);
	}

	/**
	 * base36编码。
	 * 
	 * @param hexStr
	 *          十六进制字符串。
	 * @return 经base36编码后的字符串。如果hexStr为null或长度为0，返回null。
	 */
	public static String base36Encode(String hexStr) {
		if ( hexStr == null || hexStr.length() == 0 )
			return null ;
		
		StringBuffer sb = new StringBuffer();
		
		BigInteger bi = new BigInteger(hexStr, 16);
		BigInteger d = bi;
		BigInteger m = BigInteger.ZERO;
		
		while (!BigInteger.ZERO.equals(d)) {
			m = d.mod(MODULUS);
			d = d.divide(MODULUS);
			sb.insert(0, (char) ALPHABET[m.intValue()]);
		}
		return sb.toString();
	}
	
	/**
	 * base36编码。
	 * 
	 * 例如： 
	 *   String ori = "91a6631029ce0aaad";
	 *   String test1 = Base36.encode(ori, 12);
	 *   String test2 = Base36.encode(ori, 20);
	 *   得到test1是12位长的，test2是20位长：
     *     91a6631029ce0aaad FSSOSD2L8XWT
     *     91a6631029ce0aaad 0000000ZFSSOSD2L8XWT 
	 * 
	 * @param hexStr 十六进制字符串。
	 * @param length 转码后的串长度。
	 * @return 返回指定长度的信息。如果hexStr为null或长度为0，返回null。
	 */
	public static String base36Encode(String hexStr, int length) {
		
		if (hexStr == null || hexStr.length() == 0 )
			return null;
		
		if (length > 64) {
			throw new IllegalArgumentException("参数错误（length不能超过64）:param length = "
					+ length);
		}
		String str = base36Encode(hexStr);
		if (length > str.length()) {
			str = INIT_STR.concat(str);
			str = str.substring(str.length() - length);
		}else{
			str = str.substring(str.length() - length);
		}
		return str;
	}

	/**
	 * Base36解码。
	 * 
	 * @param base36Str  base36字符串。
	 * @return 解码后的十六进制字符串。如果base36Str为null或长度为0，返回null。
	 */
	public static String base36Decode(String base36Str)  {

		//参数校验。
		if (base36Str == null || base36Str.length() == 0 )
			return null;
		
	    if ( !isValidBase32Str(base36Str)) {
			throw new IllegalArgumentException("base36字符串格式错误:\"" + base36Str+"\"");
		}
		
		//转换成大写。
		base36Str = base36Str.toUpperCase();
		
		BigInteger bi = BigInteger.ZERO;
		
		char[] chars = base36Str.toCharArray();
		
		for (int i = 0; i < base36Str.length(); i++) {
			char c = chars[i];
			int n = INVERTED_ALPHABET[c];
			bi = bi.multiply(MODULUS).add(new BigInteger("" + n));
		}

		return bi.toString(16);
	}
	
	/**
	 * URL 编码, Encode默认为UTF-8.
	 *
	 * @param s
	 *        String to be translated
	 * @return  
	 *        the translated String.  如果s为null或内容为空，返回null。     
	 */
	public static String urlEncode(String s)
	{
		return urlEncode(s, DEFAULT_URL_ENCODING);
	}

	/**
	 * URL 编码.
	 * 
	 * @param s
	 *         String to be translated
	 * @param enc 
	 *         The name of a supported character encoding
	 * @return 
	 *         the translated String.如果s为null或内容为空，返回null。         
	 */
	public static String urlEncode(String s, String enc)
	{
		if ( s == null || s.trim().length() == 0 )
			return null;
		
		try
		{
			return URLEncoder.encode(s, enc);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new IllegalArgumentException("urlEncode("+s+","+enc+")，URL编码异常!",e);
		}
	}

	/**
	 * URL 解码, Encode默认为UTF-8. 
	 * 
	 * @param encodedUrl 待解码的编码url。
	 * @return 解码后的url。如果encodedUrl为null或字符串里面没有字符，返回null。
	 */
	public static String urlDecode(String encodedUrl)
	{
		return urlDecode(encodedUrl, DEFAULT_URL_ENCODING);
	}

	/**
	 * URL 解码.
	 * 
	 * @param s   the String to decode
	 * @param enc  The name of a supported character encoding. 
	 * @return the newly decoded String.如果s为null或内容为空，返回null。   
	 */
	public static String urlDecode(String s, String enc)
	{
		if ( s == null || s.trim().length() == 0 )
			return s;
		
		try
		{
			return URLDecoder.decode(s, enc);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new IllegalArgumentException("urlEncode("+s+","+enc+")，URL解码异常!",e);
		}
	}

	/**
	 * Html转码.Escapes the characters in a String using HTML entities.
	 * 
	 * @param 
	 * 			html 待转码的字符串。
	 * @return  转码后的字符串。如果html为null或长度为0，返回null       
	 */
	public static String htmlEscape(String html)
	{
		if ( html == null || html.length() == 0 )
			return null ;
		
		return StringEscapeUtils.escapeHtml(html);
	}

	/**
	 * Html 反转码.
	 * @param escapedHtml 待反转码的字符串。
	 * @return 反转码后的字符串。如果htmlEscaped为null或长度为0，返回null。
	 */
	public static String htmlUnescape(String escapedHtml)
	{
		if ( escapedHtml == null || escapedHtml.length() ==0 )
			return null; 
		
		return StringEscapeUtils.unescapeHtml(escapedHtml);
	}

	/**
	 * Xml转码.
	 * 
	 * @param xml 待转码的xml代码。
	 * @return 转码后的字符串。如果xml为null或长度为0，返回null。   
	 */
	public static String xmlEscape(String xml)
	{
		if ( xml == null || xml.length() == 0 )
			return null;
		
		return StringEscapeUtils.escapeXml(xml);
	}

	/**
	 * Xml 反转码.
	 * 
	 * @param escapedXml 待反转码的xml字符串。
	 * @return 反转码后的字符串。如果escapedXml为null或长度为0，返回null。
	 */
	public static String xmlUnescape(String escapedXml)
	{
		if ( escapedXml == null || escapedXml.length() == 0 )
			return null;
		
		return StringEscapeUtils.unescapeXml(escapedXml);
	}

	/**
	 * 判断是否是Base32字符串。
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isValidBase32Str(String str) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (!isValidBase32Char(chars[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否是base32字符。
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isValidBase32Char(char c) {
		if ((c < 0) || (c >= 128)) {
			return false;
		} else if (INVERTED_ALPHABET[c] == -1) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) throws DecoderException{
/*		String[] str={"",null," ","123456","我们","我们123"};
		for(int i=0;i<str.length;i++){
			byte[] data= str[i] == null ? null : new String(str[i]).getBytes();
			System.out.println(str[i]);
			System.out.println("CoderUtil.hexEncode = " + hexEncode(data));
		
		}*/
//		String[] str1={"",null," ","313233343536","e68891e4bbac","e68891e4bbac313233"};
//		for(int i=0;i<str1.length;i++){
//			System.out.println(str1[i]);
//			System.out.println("CoderUtil.hexDecode = " + hexDecode(str1[i]));
//		}
		System.out.println(CodecUtil.urlDecode("%E4%BA%B2%E7%88%B1%E7%9A%84%EF%BC%8C%E6%94%B6%E5%88%B0%E5%8F%96%E7%A5%A8%E7%9F%AD%E4%BF%A1%E4%BA%86%E5%90%A7%E3%80%82%E8%AF%B7%E5%88%B0%E5%BD%B1%E9%99%A2%E5%94%AE%E7%A5%A8%E5%A4%A7%E5%8E%85%E7%BD%91%E7%A5%A8%E7%BD%91%E5%8F%96%E7%A5%A8%E6%9C%BA%E5%8F%96%E7%A5%A8%E8%A7%82%E5%BD%B1%EF%BC%8C%E9%9A%8F%E5%88%B0%E9%9A%8F%E5%8F%96%E3%80%82%E5%88%AB%E5%8E%BB%E5%94%AE%E7%A5%A8%E6%9F%9C%E5%8F%B0%E6%8E%92%E9%98%9F%E5%93%A6%EF%BC%8C%E5%8F%96%E7%A5%A8%E6%9C%BA%E4%B8%8D%E5%A5%BD%E6%89%BE%E5%B0%B1%E9%97%AE%E4%B8%8B%E5%BD%B1%E9%99%A2%E5%B7%A5%E4%BD%9C%E4%BA%BA%E5%91%98%E5%90%A7%EF%BC%81%E4%BA%B2%E4%B8%8B%E3%80%82&sendTime=20130109163006&phone=13811939741&orderId=2013010916ORDER02962815"));
		System.out.println(CodecUtil.urlDecode("zYpux5fbQtE+WBZzlPT1BQ=="));
		System.out.println(CodecUtil.urlEncode("zYpux5fbQtE+WBZzlPT1BQ=="));

		/*String[] str3={"",null,"J69DFZNLI","2HUKGGKLV0","OXH78010T9XAUR"," "};
		for(int i=0;i<str3.length;i++){
			System.out.println(str3[i]+","+"base36Decode() = " + base36Decode(str3[i]));
		}*/

		/*
		String[] str2={"",null,"313233343536","e68891e4bbac","e68891e4bbac313233"," "};
		for(int i=0;i<str2.length;i++){
			System.out.println(str2[i]+","+"base36Encode() = " + base36Encode(str2[i])
					+" , base36Encode(str,5) = " + base36Encode(str2[i],5)
					+" , base36Encode(str,16) = " + base36Encode(str2[i],16));
		}
		System.out.println(str2[2]+","+"base36Encode(str,65) = " + base36Encode(str2[2],65));
		*/

		/*String[] str3={"",null,"J69DFZNLI","2HUKGGKLV0","OXH78010T9XAUR"," "};
		for(int i=0;i<str3.length;i++){
			System.out.println(str3[i]+","+"base36Decode() = " + base36Decode(str3[i]));
		}*/
		
/*		String[] str4={"",null,"http://www.163.com","http://www.163.com?key=value"," "};
		for(int i=0;i<str4.length;i++){
			System.out.println(str4[i] +" , " + "urlEncode(str) = " + urlEncode(str4[i])
					+ " , urlEncode(str,'utf-8') = " + urlEncode(str4[i],"utf-8") );
			System.out.println(str4[i] +" , " + "urlDecode(str) = " + urlDecode(urlEncode(str4[i]))
					+ " , urlDecode(str,'utf-8') = " + urlDecode(urlEncode(str4[i],"utf-8")) );
		}*/

		/*String[] str5={"",null,"abc","<html>abc</html>"," "};
		for(int i=0;i<str5.length;i++){
			System.out.println(str5[i] +" , " + "htmlEscape(str) = " + htmlEscape(str5[i]) );
			System.out.println(str5[i] +" , " + "htmlUnescape(str) = " + htmlUnescape(str5[i]) );
		}*/
		
		/*String[] str6={"",null,"abc","<xml>abc</xml>"," "};
		for(int i=0;i<str6.length;i++){
			System.out.println(str6[i] +" , " + "xmlEscape(str) = " + xmlEscape(str6[i]) );
			System.out.println(str6[i] +" , " + "xmlUnescape(str) = " + xmlUnescape(str6[i]) );
		}*/

	}
}
