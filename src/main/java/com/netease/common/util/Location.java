package com.netease.common.util;

import java.lang.reflect.Field;


/**
 * 封装ip库的字段
 * @author 开发支持中心
 *
 */
public class Location {
	
	private String province;//省名
	private String provinceAbbr;//省名拼音
	private String provinceAreaCode;//省的电话区号
	private String city;//市名
	private String cityDivisionCode;//市行政划分码
	private String cityAreaCode;//市的电话区号
	private String operator;//运营商
	
	/**
	 * @return the province
	 */
	public String getProvince() {
	
		return province;
	}
	
	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
	
		this.province = province;
	}
	
	/**
	 * @return the provinceAbbr
	 */
	public String getProvinceAbbr() {
	
		return provinceAbbr;
	}
	
	/**
	 * @param provinceAbbr the provinceAbbr to set
	 */
	public void setProvinceAbbr(String provinceAbbr) {
	
		this.provinceAbbr = provinceAbbr;
	}
	
	/**
	 * @return the provinceAreaCode
	 */
	public String getProvinceAreaCode() {
	
		return provinceAreaCode;
	}
	
	/**
	 * @param provinceAreaCode the provinceAreaCode to set
	 */
	public void setProvinceAreaCode(String provinceAreaCode) {
	
		this.provinceAreaCode = provinceAreaCode;
	}
	
	/**
	 * @return the city
	 */
	public String getCity() {
	
		return city;
	}
	
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
	
		this.city = city;
	}
	
	/**
	 * @return the cityDivisionCode
	 */
	public String getCityDivisionCode() {
	
		return cityDivisionCode;
	}
	
	/**
	 * @param cityDivisionCode the cityDivisionCode to set
	 */
	public void setCityDivisionCode(String cityDivisionCode) {
	
		this.cityDivisionCode = cityDivisionCode;
	}
	
	/**
	 * @return the cityAreaCode
	 */
	public String getCityAreaCode() {
	
		return cityAreaCode;
	}
	
	/**
	 * @param cityAreaCode the cityAreaCode to set
	 */
	public void setCityAreaCode(String cityAreaCode) {
	
		this.cityAreaCode = cityAreaCode;
	}
	
	/**
	 * @return the operator
	 */
	public String getOperator() {
	
		return operator;
	}
	
	/**
	 * @param operator the operator to set
	 */
	public void setOperator(String operator) {
	
		this.operator = operator;
	}
		
	public String toString() {

		StringBuffer sb = new StringBuffer("[");
		Field fields[] = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				sb.append(field.getName()).append("=").append(field.get(this)).append(",");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
}
