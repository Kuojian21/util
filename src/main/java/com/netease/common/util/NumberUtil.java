package com.netease.common.util;	 

import java.math.BigDecimal;
import java.text.DecimalFormat;
/**
 * 数字格式化工具类
 * 
 * @author 开发支持中心。
 */
public class NumberUtil{
	 
	/**
	 * 格式化BigDecimal对象。
	 * 
	 * @param big
	 *        待格式化的BigDecimal对象。
	 * @param format
	 *        格式。
	 * @return
	 *        格式化之后的字符串。
	 */
	public static String format(BigDecimal big,String format)
	{
		if (big == null)
		{
			return null;
		}
		DecimalFormat df = new DecimalFormat(format);
		return df.format(big);
	}
}