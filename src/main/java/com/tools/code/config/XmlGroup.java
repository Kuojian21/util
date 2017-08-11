package com.tools.code.config;

public class XmlGroup  implements Group{
	public final static Type mybatis = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/xml/mybatis.tpl";
		}

		public String pkg() {
			return "cron";
		}

		public String suffix() {
			return "Cron";
		}

		@Override
		public String path(String base) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String name() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String ext() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	@Override
	public Type[] types() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String path() {
		// TODO Auto-generated method stub
		return null;
	}
}
