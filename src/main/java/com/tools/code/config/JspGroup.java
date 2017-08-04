package com.tools.code.config;

public class JspGroup  implements Group{
	public final static Type ADD = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/jsp/add.tpl";
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
	public final static Type LIST = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/jsp/list.tpl";
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
	public final static Type EDIT = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/jsp/edit.tpl";
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
	
	public final static Type VIEW = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/jsp/view.tpl";
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
