package com.tools.code;

public class Property {
	/** 属性名称 */
	private String name;
	/** 属性类型 */
	private String type;
	/** mybatis中javaType */
	private String mtype;
	/** 列名称 */
	private String cname;
	/** 主键*/
	private boolean ckey;
	/** 列类型 */
	private String ctype;
	/** 列注释 */
	private String ccomment;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public boolean getCkey() {
		return ckey;
	}
	public void setCkey(boolean ckey) {
		this.ckey = ckey;
	}
	public String getCcomment() {
		return ccomment;
	}
	public void setCcomment(String ccomment) {
		this.ccomment = ccomment;
	}
	public String getMtype() {
		return mtype;
	}
	public void setMtype(String mtype) {
		this.mtype = mtype;
	}
}
