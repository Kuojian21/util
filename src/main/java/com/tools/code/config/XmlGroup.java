package com.tools.code.config;

public class XmlGroup  implements Group{
	public final static Type mybatis = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/xml/mybatis.tpl";
		}

		@Override
		public String pkg() {
			return "cron";
		}

		@Override
		public String suffix() {
			return "Cron";
		}
	};
}
