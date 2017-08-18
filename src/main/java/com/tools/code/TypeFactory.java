package com.tools.code;

public class TypeFactory {

	public static abstract class JavaType implements Type {
		private Model model;

		public JavaType(Model model) {
			super();
			this.model = model;
		}

		public abstract String subPkg();

		public abstract String nameSuffix();

		@Override
		public String path() {
			return this.model().getBase() + "/src/java/main/" + this.model().getPkg().replaceAll("\\.", "/") + "/"
					+ this.subPkg();
		}

		@Override
		public String name() {
			return this.path() + "/" + this.model.getName() + this.nameSuffix() + ".java";
		}

		@Override
		public Model model() {
			return this.model;
		}

	}

	public static Type model(Model model) {
		return new JavaType(model) {
			@Override
			public String tpl() {
				return "/velocity/template/javacode/model.tpl";
			}

			@Override
			public String subPkg() {
				return "model";
			}

			@Override
			public String nameSuffix() {
				return "";
			}
		};
	}

	public static Type dao(Model model) {
		return new JavaType(model) {
			@Override
			public String tpl() {
				return "/velocity/template/javacode/dao.tpl";
			}

			@Override
			public String subPkg() {
				return "dao";
			}

			@Override
			public String nameSuffix() {
				return "Dao";
			}
		};
	}

	public static Type daoImpl(Model model) {
		return new JavaType(model) {
			@Override
			public String tpl() {
				return "/velocity/template/javacode/dao_impl.tpl";
			}

			@Override
			public String subPkg() {
				return "dao.impl";
			}

			@Override
			public String nameSuffix() {
				return "DaoImpl";
			}
		};
	}

	public static Type service(Model model) {
		return new JavaType(model) {
			@Override
			public String tpl() {
				return "/velocity/template/javacode/service.tpl";
			}

			@Override
			public String subPkg() {
				return "service";
			}

			@Override
			public String nameSuffix() {
				return "Service";
			}
		};
	}

	public static Type serviceImpl(Model model) {
		return new JavaType(model) {
			@Override
			public String tpl() {
				return "/velocity/template/javacode/service_impl.tpl";
			}

			@Override
			public String subPkg() {
				return "service.impl";
			}

			@Override
			public String nameSuffix() {
				return "ServiceImpl";
			}
		};
	}

	public static Type controller(Model model) {
		return new JavaType(model) {
			@Override
			public String tpl() {
				return "/velocity/template/javacode/controller.tpl";
			}

			@Override
			public String subPkg() {
				return "controller";
			}

			@Override
			public String nameSuffix() {
				return "Controller";
			}
		};
	}

	public static Type cron(Model model) {
		return new JavaType(model) {
			@Override
			public String tpl() {
				return "/velocity/template/javacode/cron.tpl";
			}

			@Override
			public String subPkg() {
				return "cron";
			}

			@Override
			public String nameSuffix() {
				return "Cron";
			}
		};
	}

	public static Type mybatis(Model model) {
		return new Type() {
			@Override
			public String path() {
				return this.model().getBase() +"/java/main/resources/mybatis";
			}

			@Override
			public String tpl() {
				return "/velocity/template/xml/mybatis.tpl";
			}

			@Override
			public String name() {
				return this.path() + "/" + this.model().getName() + "DaoImpl.xml";
			}

			@Override
			public Model model() {
				return model;
			}
		};
	}

}
