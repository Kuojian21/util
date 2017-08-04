package com.tools.code.config;



public class JavaGroup implements Group{

	public final static Type MODEL = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/javacode/model.tpl";
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
	
	public final static Type DAO = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/javacode/dao.tpl";
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
	
	public final static Type DAO_IMPL = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/javacode/dao_impl.tpl";
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
	
	public final static Type SERVICE = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/javacode/service.tpl";
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
	
	public final static Type SERVICE_IMPL = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/javacode/service_impl.tpl";
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
	
	public final static Type CONTROLLER = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/javacode/controller.tpl";
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
	
	public final static Type CRON = new Type(){
		@Override
		public String tpl() {
			return "/velocity/template/javacode/cron.tpl";
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

	public final static Type[] types = new Type[]{
		MODEL,DAO,DAO_IMPL,SERVICE,SERVICE_IMPL,CONTROLLER,CRON
	};
	
	@Override
	public Type[] types() {
		return types;
	}
	

}
