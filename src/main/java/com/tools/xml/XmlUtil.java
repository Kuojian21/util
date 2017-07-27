package com.tools.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultAttribute;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.netease.common.util.CodecUtil;
import com.netease.common.util.LogUtil;
import com.netease.common.util.StringUtil;

/**
 * map <-> xml转换工具，提供如下接口：<br/>
 * 1. bean与XML文档之间转换方法；<br/>
 * 2. map与XML文档之间转换方法等。<br/> 
 * 
 * @author 开发支持中心
 * 
 */
public class XmlUtil {

	/**
	 * map转换为Dom4j的Element
	 * 
	 * @param input
	 *            要转换的map
	 * @param root
	 *            root==null生成（构成树型结构的Map的）根节点， root!=null生成非根节点
	 * @param rootName
	 *            根节点名字。只在最上层使用
	 * @return map对应的elment对象
	 */
	public static Element mapToXmlElement(Map input, Element root, String rootName) {
		Document document = DocumentHelper.createDocument();
		if (null == root) {
			root = document.addElement(rootName);
		}

		Iterator<Entry> itrInput = input.entrySet().iterator();

		boolean hasAttr = false;
		while (itrInput.hasNext()) {
			/* 从Map取出一个属性 */
			Entry entry = itrInput.next();
			String key = entry.getKey().toString();// 
			Object value = entry.getValue();

			// 带属性的情况。^开头的key是属性(取值只能为string)，@+本标签key是 本标签为页节点时去掉属性得到的标签。
			if (key.indexOf("^") == 0) {
				root.addAttribute(key.substring(1), value.toString());
				hasAttr = true;
				continue;
			}
			if (key.indexOf("@") == 0) {
				root.setText(value.toString());
				continue;
			}

			/* 添加到xml中 */
			if (value instanceof Map) {
				Element subElement = root.addElement(key);
				mapToXmlElement((Map) value, subElement, null);
			} else if (value instanceof List) {// 下级解析返回一个
				Element subElement = root.addElement(key);
				listToXmlElement((List) value, subElement, key, null);
			} else if (value instanceof Persistent) {
				Element subElement = root.addElement(key);
				if (null == value) {
					// subElement.addText("null");
					continue;
				}

				Map map = ((Persistent) value).toMap();
				mapToXmlElement(map, subElement, null);
			} else {
				Element subElement = root.addElement(key);
				String valueToString = null;
				if (null == value) {
					// valueToString = "null";
					continue;
				} else {
					valueToString = value.toString();
				}

				if ((valueToString.indexOf("<") >= 0 && valueToString.indexOf("</") >= 0) || valueToString.indexOf("/>") >= 0) {
					CDATA cdata = DocumentHelper.createCDATA(valueToString);
					subElement.add(cdata);
				} else {
					subElement.addText(valueToString);
				}
			}
		}

		if (true == hasAttr) {
			root.setName(root.getName().substring(0, root.getName().indexOf(" ")));
		}

		return root;
	}

	/**
	 * list转换为Dom4j的Element
	 * 
	 * @param input
	 *            要转换的map
	 * @param root
	 *            root==null生成（构成树型结构的Map的）根节点， root!=null生成非根节点
	 * @param key
	 * @param rootName
	 *            根节点名字。只在最上层使用
	 * @return list对应的element对象
	 */
	public static Element listToXmlElement(List input, Element root, String key, String rootName) {
		Document document = DocumentHelper.createDocument();
		if (null == root) {
			root = document.addElement(rootName);
		}

		Iterator itrInput = input.iterator();

		while (itrInput.hasNext()) {
			/* 从List取出一个属性 */
			Object value = itrInput.next();

			/* 添加到xml中 */
			Pattern pattern = Pattern.compile("(\\w+)List");
			Matcher matcher = pattern.matcher(root.getName());
			String elementName = "element";
			if (matcher.matches()) {
				elementName = matcher.group(1);
			}
			Element subElement = root.addElement(elementName);
			if (value instanceof Map) {
				// Element subElement = root.addElement(key);
				mapToXmlElement((Map) value, subElement, null);
			} else if (value instanceof List) {// 下级解析返回一个
				// 程序错误
				listToXmlElement((List) value, subElement, key, null);
			} else if (value instanceof Persistent) {
				if (null == value) {
					subElement.addText(null);
					continue;
				}
				Map map = ((Persistent) value).toMap();
				mapToXmlElement(map, subElement, key);
			} else {
				if (null == value) {
					// value = "null";
					continue;
				}
				String v = value.toString();

				subElement.addText(value.toString());
			}
		}

		return root;
	}

	/**
	 * map转换为string
	 * 
	 * @param input
	 *            输入的Map
	 * @return
	 *          map对应的XML字符串
	 */
	public static String mapToXml(Map input, String rootName) {
		return mapToXmlElement(input, null, rootName).asXML();
	}

	/**
	 * 字符串转换为List<Map>
	 * 
	 * @param input
	 * @return input对应的List对象
	 * @throws DocumentException
	 */
	public static List xmlToListOfMap(String input) throws DocumentException {
		List result = null;

		/* 字符串形式的xml -> dom4j的xml */
		Document document = DocumentHelper.parseText(input);

		/* 遍历dom4j xml, 生成map */
		result = new ArrayList();
		Element element = document.getRootElement();
		List<Element> subElementList = element.elements();
		for (int i = 0; i < subElementList.size(); i++) {
			Element e = subElementList.get(i);
			Object nodeOfList = xmlElementToMap(e);
			result.add(nodeOfList);
		}

		return result;
	}

	/**
	 * xml字符串 -〉 map
	 * 
	 * @param element
	 * @return element对应的map对象
	 * @throws DocumentException
	 */
	public static Object xmlElementToMap(Element element) throws DocumentException {
		List<DefaultAttribute> attrList = element.attributes();

		Map result = new HashMap();
		List<Map> tmpList = null;// 处理过程中不能分辨map或list,先统一照List处理

		/* 遍历dom4j xml, 生成map */
		String name = element.getName();

		List<Element> elementList = element.elements();

		if (elementList.size() == 0 && attrList.size() == 0) {// 叶节点,且无
			String value = element.getText().trim();
			// 去掉<![CDATA[]]
			if (value.indexOf("<![CDATA[") == 0 && value.indexOf("]]") + 2 == value.length() - 1) {
				value = value.substring(9, value.length() - 2);
				value = "<" + element.getName() + ">" + value + "<" + element.getName() + "/>";
			}

			return value;
		} else {// 非叶节点
			// List<Element> elementList = element.elements();
			if (elementList.size() == 0) {// 页节点，但有属性
				// 创建一个下级映射，其key为"@"+该标名称
				String value = element.getText().trim();
				// 去掉<![CDATA[]]
				if (value.indexOf("<![CDATA[") == 0 && value.indexOf("]]") + 2 == value.length() - 1) {
					value = value.substring(9, value.length() - 2);
				}

				result.put("@" + element.getName(), value);
			} else if (name.toLowerCase().indexOf("list") >= 0)// ..to be
																// corrected..
			{// 队列
				tmpList = new ArrayList<Map>();
				for (int i = 0; i < elementList.size(); i++) {
					Element subElement = elementList.get(i);

					// LogUtil..debug("当前节点信息："+subElement.asXML());
					Object curElement = xmlElementToMap(subElement);
					Map node = null;
					if (curElement instanceof String) {
						node = new HashMap();
						// LogUtil..debug("节点名称："+subElement.getName()+";节点值:"+subElement.getData());
						node.put(subElement.getName(), subElement.getData());
					} else {
						node = (Map) curElement;
					}

					tmpList.add(node);
				}

				return tmpList;
			} else {// 映射
				for (int i = 0; i < elementList.size(); i++) {
					Element subElement = elementList.get(i);
					Object node = xmlElementToMap(subElement);

					List<DefaultAttribute> subAttrList = subElement.attributes();
					if (0 == subAttrList.size()) {
						result.put(subElement.getName(), node);
						continue;
					}

					// 有属性时需要改名
					// <relation a=\"a\" b=\"b\"> => 新key = "relation a=a b=b"
					String newKey = subElement.getName();

					Iterator<DefaultAttribute> itr = subAttrList.iterator();
					while (itr.hasNext()) {
						DefaultAttribute attr = itr.next();
						newKey += " " + attr.getName() + "=" + attr.getValue();
					}
					result.put(newKey, node);
				}
			}
		}

		// 有属性.
		if (attrList.size() == 0) {
			return result;
		}

		// 加上attr
		Iterator<DefaultAttribute> itr = attrList.iterator();
		while (itr.hasNext()) {
			DefaultAttribute attr = itr.next();
			result.put("^" + attr.getName(), attr.getValue());// ^表示是attr
		}

		return result;
	}

	public static Map xmlToMap(String xml) throws DocumentException {
		return xmlToMap(xml, null);
	}

	public static Map xmlToMap(String xml, List<String> escapeList) throws DocumentException {
		if (null == xml) {
			return null;
		}
		if ("".equalsIgnoreCase(xml)) {
			return null;
		}

		Map result = null;

		/* 把不需要转换的标签放到<![CDATA[ ]]>里 */
		for (int i = 0; null != escapeList && i < escapeList.size(); i++) {
			String escapeTag = escapeList.get(i);

			// 如果已在<![CDATA[ ]]>中，则不修改
			String subStr = xml.substring(xml.indexOf("<" + escapeTag + ">"), xml.indexOf("</" + escapeTag + ">"));
			if (subStr.indexOf("<![CDATA[") >= 0) {
				continue;
			}

			xml = xml.replaceAll("<" + escapeTag + ">", "<" + escapeTag + ">" + "<![CDATA[");
			xml = xml.replaceAll("</" + escapeTag + ">", "]]></" + escapeTag + ">");
		}

		/* 字符串形式的xml -> dom4j的xml */
		//LogUtil..debug(xml);
		Document document = DocumentHelper.parseText(xml);

		/* 遍历dom4j xml, 生成map */
		Element element = document.getRootElement();

		// 最上层一定只有一个元素
		Object ret = xmlElementToMap(element);
		if (ret instanceof String) {
			result = new HashMap();
		} else if (ret instanceof Map) {
			result = (Map) ret;
		} else {
			throw new DocumentException("xml return type error:" + ret.getClass());
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map searchElementsToMap(String xml, String xpath, String keyName, String valueName) {
		try {
			if (null == xml) {
				return null;
			}
			if ("".equalsIgnoreCase(xml)) {
				return null;
			}

			Map result = null;

			Document document = null;
			List elements = null;

			document = DocumentHelper.parseText(xml);

			if (null != document) {
				Element element = document.getRootElement();
				if (null == element) {
					return null;
				}
				if (xpath.split("/").length > 3) {// 3层
					List<Element> subElementList = element.elements();
					if (null != subElementList && subElementList.size() > 0) {
						elements = subElementList.get(0).elements();
					}
				} else {// 2层
					elements = element.elements();
				}

				if (null != elements && elements.size() > 0) {
					result = new HashMap();
					Iterator it = elements.iterator();
					while (it.hasNext()) {
						Element elt = (Element) it.next();
						Attribute nameAttribute = elt.attribute(keyName);
						Attribute valueAttribute = elt.attribute(valueName);
						result.put(nameAttribute.getValue(), valueAttribute.getValue());
					}
				}
			}

			return result;
		} catch (DocumentException e) {
			//e.printStackTrace();
			return null;
		}
	}

	private final static Map<Class<?>, Class<?>> SIMPLE_TYPE = new HashMap<Class<?>, Class<?>>(
			16);

	static {
		SIMPLE_TYPE.put(boolean.class, Boolean.class);
		SIMPLE_TYPE.put(byte.class, Byte.class);
		SIMPLE_TYPE.put(char.class, Character.class);
		SIMPLE_TYPE.put(double.class, Double.class);
		SIMPLE_TYPE.put(float.class, Float.class);
		SIMPLE_TYPE.put(int.class, Integer.class);
		SIMPLE_TYPE.put(long.class, Long.class);
		SIMPLE_TYPE.put(Short.class, short.class);
		SIMPLE_TYPE.put(BigDecimal.class, BigDecimal.class);
		SIMPLE_TYPE.put(BigInteger.class, BigInteger.class);
		SIMPLE_TYPE.put(String.class, String.class);
		SIMPLE_TYPE.put(void.class, Void.class);
		SIMPLE_TYPE.put(Enum.class, Enum.class);
	}

	public static boolean isSimpleElementType(Class<?> clazz) {
		return SIMPLE_TYPE.keySet().contains(clazz)
				|| SIMPLE_TYPE.values().contains(clazz);
	}
	

	/**
	 * added by chenglei
	 * 
	 * @param rootName
	 * @param bean
	 * @return bean对应的String字符串
	 */
	public static String beanToXmlString(String rootName, Object bean) {
		Document doc = DocumentHelper.createDocument();
		Element rootElement = doc.addElement(rootName);
		XmlUtil.beanToXmlElement(rootElement, bean);
		return documentToString(doc, true);

	}

	/**
	 * added by chenglei
	 * 
	 * @param parentElement
	 * @param bean
	 * 
	 */
	public static void beanToXmlElement(final Element parentElement, final Object bean) {
		if (Collection.class.isAssignableFrom(bean.getClass()) || bean.getClass().isArray()) {
			throw new UnsupportedOperationException("Collection");

		} else if (Map.class.isAssignableFrom(bean.getClass())) {
			throw new UnsupportedOperationException("Map");
		}

		ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {

			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				String filedName = field.getName();

				ReflectionUtils.makeAccessible(field);
				Object fieldValue = ReflectionUtils.getField(field, bean);
				Element currentElement = parentElement.addElement(filedName);

				if (isSimpleElementType(field.getType()) || fieldValue == null) {

					String strValue = nullSafeToString(fieldValue);

					currentElement.setText(strValue);
				} else {
					beanToXmlElement(currentElement, fieldValue);
				}

			}
		});

	}

	/**
	 * @param parentElement
	 * @param bean
	 *
	 */
	public static void beanToXmlElement(final Element parentElement, final Object bean, final List<String> noToProcessedFieldList) {
		if (Collection.class.isAssignableFrom(bean.getClass()) || bean.getClass().isArray()) {
			throw new UnsupportedOperationException("Collection");

		} else if (Map.class.isAssignableFrom(bean.getClass())) {
			throw new UnsupportedOperationException("Map");
		}

		ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {

			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				String filedName = field.getName();
				if (!noToProcessedFieldList.contains(filedName)) {
					ReflectionUtils.makeAccessible(field);
					Object fieldValue = ReflectionUtils.getField(field, bean);

					Element currentElement = parentElement.addElement(filedName);

					if (isSimpleElementType(field.getType()) || fieldValue == null) {

						String strValue = nullSafeToString(fieldValue);

						currentElement.setText(strValue);
					} else {
						beanToXmlElement(currentElement, fieldValue);
					}
				}
			}
		});

	}

	private static String nullSafeToString(Object e) {
		if (e == null)
			return "";

		if (e.getClass() == BigDecimal.class) {
			BigDecimal decimal = (BigDecimal) e;
			return decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString();

		}

		return e.toString();
	}

	public static String documentToString(Document doc, boolean declaration) {

		StringWriter innerWriter = new StringWriter();
		OutputFormat format = new OutputFormat();
		if (!declaration) {
			format.setSuppressDeclaration(true);
		}
		XMLWriter xmlWriter = new XMLWriter(innerWriter, format);
		try {

			xmlWriter.write(doc);
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				xmlWriter.close();
			} catch (IOException e) {

			}
		}

		return innerWriter.toString();
	}

	/**
	 * 把object转换为map
	 * 
	 * @param object
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws DocumentException
	 * @return object对应的map对象
	 */
	public static Map objectToMap(Object object) throws SecurityException, NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, DocumentException {
		Method[] methodArray = object.getClass().getMethods();

		String fieldName = null;
		String methodName = null;// get方法名
		Map fieldValue = null;// field取值
		Object fieldObject = null;
		Map result = new HashMap();// 结果
		for (int i = 0; i < methodArray.length; i++) {
			// 生成方法名 getXxxx.
			methodName = methodArray[i].getName();
			if (!methodName.startsWith("get")) {
				continue;
			}
			fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);

			// 获取方法
			Method method = object.getClass().getMethod(methodName);

			// 调用方法，取得参数值
			fieldObject = method.invoke(object);
			if (null == fieldObject) {
				continue;
			}

			if (fieldObject instanceof Persistent) {
				fieldValue = objectToMap(fieldObject);
				result.put(fieldName, fieldValue);
			} else if (fieldObject.toString().indexOf("</") == 0) {
				// xml转换为map
				result.put(fieldName, XmlUtil.xmlToMap(fieldObject.toString()));
			} else {
				result.put(fieldName, fieldObject.toString());
			}

		}

		return result;
	}

	/**
	 * 装配只有简单属性的map数据。支持继承关系。暂不支持多个同name控件
	 * 
	 * @param object
	 *            OUT object 要加载数据的Form对象。必须先创建
	 * @return 成功 success 失败 error
	 */
	public static String parseSimpleMap(Map map, Object object) {
		try {
			Method method = null;// set方法
			String param = null;// web参数的取值
			String fieldType = null;// gameChargeLog的某个属性的类型
			String methodName = null;// gameChargeLog的某个set方法名
			String fieldName = null;

			Class<?> classObject = object.getClass();

			do {
				// Field[] arrayField = classObject.getDeclaredFields();
				Method[] arrayMethod = classObject.getDeclaredMethods();

				for (int i = 0; i < arrayMethod.length; i++) {
					// 生成方法名 getXxxx.
					methodName = arrayMethod[i].getName();
					if (!methodName.startsWith("set")) {
						continue;
					}
					fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);

					// 调用set方法
					// 1.确定参数类型。代码不严格，有待改进
					Field field = classObject.getDeclaredField(fieldName);
					if (null == field) {
						continue;
					}

					// 取得web数据
					Object oPara = map.get(fieldName);
					if (oPara instanceof String) {
						param = (String) oPara;
					} else if (oPara instanceof Map) {
						param = XmlUtil.mapToXml((Map) oPara, fieldName);
					} else {
						continue;
					}

					fieldType = field.getType().getName();
					if (fieldType.lastIndexOf("Integer") > 0) {
						method = classObject.getDeclaredMethod(methodName, Integer.class);

						if (null != param && !"".equalsIgnoreCase(param)) {
							method.invoke(object, Integer.parseInt(param));
						}
					} else if (fieldType.lastIndexOf("Timestamp") > 0) {
						method = classObject.getDeclaredMethod(methodName, Timestamp.class);
						if (null != param && !"".equalsIgnoreCase(param)) {
							if(param.contains(":")){
								method.invoke(object, Timestamp.valueOf(param + ".000000000"));
							}else{
								method.invoke(object, Timestamp.valueOf(param + " 00:00:00.000000000"));
							}
						}
					} else if (fieldType.lastIndexOf("BigDecimal") > 0) {
						method = classObject.getDeclaredMethod(methodName, BigDecimal.class);
						if (null != param && !"".equalsIgnoreCase(param)) {
							method.invoke(object, BigDecimal.valueOf(Double.parseDouble(param)));
						}
					} else if (fieldType.lastIndexOf("List") > 0) {
						continue;// 暂不实现
					} else {// default: plain copy
						method = classObject.getDeclaredMethod(methodName, String.class);
						method.invoke(object, param);
					}
				}// for

				// superior class
				classObject = classObject.getSuperclass();
			} while (null != classObject);
		} catch (Exception e) {
			LogUtil.error("Parse form param error:", e);
			return "error";
		}

		return "success";
	}

	public static String append(String first, String second) throws DocumentException {
		if (null == first || "".equalsIgnoreCase(first)) {
			return second;
		}
		if (null == second || "".equalsIgnoreCase(second)) {
			return first;
		}

		Map newMap = XmlUtil.xmlToMap(first);
		Map oldMap = XmlUtil.xmlToMap(second);
		if (null == oldMap) {
			oldMap = new HashMap();
		}
		oldMap.putAll(newMap);
		String result = XmlUtil.mapToXml(oldMap, "XX");
		return result;
	}
}
/**
 * 序列化为HTTP参数的类
 * @author Administrator
 *
 */
class Persistent {
	
	public Map toMap() {
		Map ret = null;
		
		try {
			ret = XmlUtil.objectToMap(this);
		} catch (SecurityException e) {
			LogUtil.error(this.getClass().getName() + "toMap():", e);
		} catch (IllegalArgumentException e) {
			LogUtil.error(this.getClass().getName() + "toMap():", e);
		} catch (NoSuchMethodException e) {
			LogUtil.error(this.getClass().getName() + "toMap():", e);
		} catch (IllegalAccessException e) {
			LogUtil.error(this.getClass().getName() + "toMap():", e);
		} catch (InvocationTargetException e) {
			LogUtil.error(this.getClass().getName() + "toMap():", e);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ret.remove("class");
		ret.remove("signature");
		
		return ret;
	}
	
	@Override
	public String toString(){
		String ret = null;
		try {
			ret = Formatter.objectToHttpParam(this);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
}

/**
 * 数据格式转换。以后会有独立的包，包含若干个类，实现统一接口。
 * @author Administrator
 *
 */
class Formatter {

	/**
	 * object转化为HTTP参数。性能瓶颈在网络通信上，反射影响不大
	 * @param object
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static String objectToHttpParam(Object object,boolean isRemoveNull) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Field[] fieldArray = object.getClass().getDeclaredFields();
		String result = "?";//返回值
		
		String methodName = null;//get方法名
		String fieldValue = null;//field取值
		Object fieldObject = null;
		for(int i = 0; i < fieldArray.length; i ++){
			//生成方法名 getXxxx.
			methodName = fieldArray[i].getName();
			methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
			methodName = "get" + methodName;
			
			//获取方法
			Method method = null;
			try
			{
				method = object.getClass().getMethod(methodName);
				fieldObject = null;
				if (method != null)
				{
					//调用方法，取得参数值
					fieldObject = method.invoke(object);
				}
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
			if(null == fieldObject){
				continue;
			}
			fieldValue = fieldObject.toString();
			
			if(isRemoveNull&&StringUtil.isEmpty(fieldValue)){
				continue;
			}else{
				//拼接url参数
				result += fieldArray[i].getName().toLowerCase();
				result += "=";
				result += CodecUtil.urlEncode(fieldValue, "GBK");
				result += "&";
			}
			
		}
		
		//去掉最后一个&
		result = result.substring(0, result.length() - 1);
		
		return result;
	}
	public static String objectToHttpParam(Object object) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		return objectToHttpParam(object,false);
	}


    /**
     * url参数字符串转化为Map
     * @param condition
     * @return
     */
	public static Map<String, String> urlParam2Map(String urlParam) {
		Map<String, String> result = new HashMap();
		//1. p1=a&p2=3&p3=4 => p1=a; p2=3; p3=4;
		String[] urlParams = urlParam.split("&");
		
		//2.p1=a => key=p1; value=a;
		for(int i = 0; i < urlParams.length; i ++){
			String[] params = urlParams[i].split("\\=");
			if(params.length != 2){continue;}
			result.put(params[0], params[1]);
		}
		
		return result;
	}
}
