package com.tools.code.config;

public class JspGroup  implements Group{
	public final static Type ADD = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/jsp/add.tpl";
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
	public final static Type LIST = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/jsp/list.tpl";
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
	public final static Type EDIT = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/jsp/edit.tpl";
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
	
	public final static Type VIEW = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/jsp/view.tpl";
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
