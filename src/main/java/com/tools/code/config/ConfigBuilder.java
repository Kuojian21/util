package com.tools.code.config;


public class ConfigBuilder {

	public final static Config MODEL = new Config(){
		@Override
		public String tpl() {
			return "/velocity/template/code/model.tpl";
		}

		@Override
		public String pkg() {
			return "model";
		}

		@Override
		public String suffix() {
			return "";
		}
		
	};
	
	public final static Config DAO = new Config(){
		@Override
		public String tpl() {
			return "/velocity/template/code/dao.tpl";
		}

		@Override
		public String pkg() {
			return "dao";
		}

		@Override
		public String suffix() {
			
			return "Dao";
		}

	};
	
	public final static Config DAO_IMPL = new Config(){
		@Override
		public String tpl() {
			return "/velocity/template/code/dao_impl.tpl";
		}

		@Override
		public String pkg() {
			return "dao.impl";
		}

		@Override
		public String suffix() {
			
			return "DaoImpl";
		}

	};
	
	public final static Config SERVICE = new Config(){
		@Override
		public String tpl() {
			return "/velocity/template/code/service.tpl";
		}

		@Override
		public String pkg() {
			return "service";
		}

		@Override
		public String suffix() {
			return "Service";
		}
		
	};
	
	public final static Config SERVICE_IMPL = new Config(){
		@Override
		public String tpl() {
			return "/velocity/template/code/service_impl.tpl";
		}

		@Override
		public String pkg() {
			return "service.impl";
		}

		@Override
		public String suffix() {
			return "ServiceImpl";
		}
		
	};
	
	public final static Config CONTROLLER = new Config(){
		@Override
		public String tpl() {
			return "/velocity/template/code/controller.tpl";
		}

		@Override
		public String pkg() {
			return "controller";
		}

		@Override
		public String suffix() {
			return null;
		}

	};
	
	public final static Config CRON = new Config(){
		@Override
		public String tpl() {
			return "/velocity/template/code/cron.tpl";
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
