package com.java.kj.base;

public abstract class KjPool<T, R> {

	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					KjPool.this.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected abstract T borrowObject() throws Exception;

	protected abstract void returnObject(T obj) throws Exception;

	protected abstract void close() throws Exception;

	public interface Action<T, R> {
		R action(T obj, Object... objs) throws Exception;
	}

	public final R execute(Action<T, R> action,Object... objs) throws Exception {
		T obj = null;
		try {
			obj = this.borrowObject();
			return action.action(obj,objs);
		} finally {
			if (obj != null) {
				this.returnObject(obj);
			}
		}
	}

}
