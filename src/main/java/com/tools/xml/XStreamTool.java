package com.tools.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.mapper.MapperWrapper;
/**
 * 基于第三方工具XStream简单封装XML处理工具类
 * 使用该类需要自行根据XML文档结构，自动生成对应的Java实体类<br/>
 * 示例1：<br/>
 * 通过XML文档获取Java实体对象的方式<br/>
 * 其中MiscXMLEntity是与misc XML文档对应的，自动生成的Java对象   <br/>
	public static MiscXMLEntity getMiscXMLEntity(String xml) {<br/>
		Object object = getEntity(xml, MiscXMLEntity.class);<br/>
		if (object instanceof MiscXMLEntity) {<br/>
			return (MiscXMLEntity) object;<br/>
		}<br/>
		return null;<br/>
	}<br/>
 * 示例2：通过java实体对象获取XML文档的方式<br/>
	public static String getMiscXML(MiscXMLEntity entity) {<br/>
		return getXML(entity, MiscXMLEntity.class);<br/>
	} <br/>
 * @author 开发支持中心
 */
public class XStreamTool {

	private static String DEFAULT_ENCODING = "UTF-8";
	
    /**
     * 对外接口：通过XML文档，获取Java对象
     * @param xml 			XML文档字符串
     * @param classType		Java实体类名称
     * @return xml对应的object对象
     */
	@SuppressWarnings("rawtypes")
	public static Object getEntity(String xml, Class classType) {
		if (xml == null || xml.length() == 0) {
			return null;
		}
		XStream xstream = getXStream(DEFAULT_ENCODING);
		xstream.processAnnotations(classType);
		return xstream.fromXML(xml);
	}
	/**
	 * 
     * 对外接口：通过Java对象，生成XML文档字符串      
	 * @param object			Java实体对象
	 * @param classType			实体对象类名称
	 * @return object对应的XML文档
	 */
	@SuppressWarnings("rawtypes")
	public static String getXML(Object object, Class classType) {
		XStream xstream = getXStream(DEFAULT_ENCODING);
		xstream.processAnnotations(classType);
		return xstream.toXML(object);
	}   

    /**
     * 获取XStream对象，用于后续操作
     */
	private static XStream getXStream(String encode) {
		XStream xstream = new XStream(new DomDriver(encode, new XmlFriendlyNameCoder("_-", "_"))) {
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				return new MapperWrapper(next) {
					public boolean shouldSerializeMember(Class definedIn, String fieldName) {
						return definedIn != Object.class ? super.shouldSerializeMember(definedIn, fieldName) : false;
					}
				};
			}
		};
		return xstream;
	}
}
