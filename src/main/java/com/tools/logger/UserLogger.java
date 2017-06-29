package com.tools.logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class UserLogger extends Logger {

	/**
	 * The fully qualified name of the Category class. See also the getFQCN
	 * method.
	 */
	private static final String FQCN = UserLogger.class.getName();
	private static final LoggerFactory factory = new UserLogFactory();

	UserLogger(String name) {
		super(name);
	}

	public static UserLogger getLogger(String name) {
		return (UserLogger) LogManager.getLogger(name,factory);
	}

	public void debug(String module, Object message, Object... params) {
		Object messagelog = decorateMessage(module, message, params);
		if (repository.isDisabled(Priority.DEBUG_INT))
			return;
		if (Level.DEBUG.isGreaterOrEqual(getEffectiveLevel())) {
			forcedLog(FQCN, Level.DEBUG, messagelog, null);
		}
	}

	public void debug(String module, Object message, Throwable t, Object... params) {
		Object messagelog = decorateMessage(module, message, params);
		if (repository.isDisabled(Priority.DEBUG_INT))
			return;
		if (Level.DEBUG.isGreaterOrEqual(getEffectiveLevel())) {
			forcedLog(FQCN, Level.DEBUG, messagelog, t);
		}
	}

	public void info(String module, Object message, Object... params) {
		Object messagelog = decorateMessage(module, message, params);
		if (repository.isDisabled(Level.INFO_INT))
			return;
		if (Level.INFO.isGreaterOrEqual(this.getEffectiveLevel()))
			forcedLog(FQCN, Level.INFO, messagelog, null);
	}

	public void info(String module, Object message, Throwable t, Object... params) {
		Object messagelog = decorateMessage(module, message, params);
		if (repository.isDisabled(Level.INFO_INT))
			return;
		if (Level.INFO.isGreaterOrEqual(this.getEffectiveLevel()))
			forcedLog(FQCN, Level.INFO, messagelog, t);
	}

	public void error(String module, Object message, Object... params) {
		Object messagelog = decorateMessage(module, message, params);
		if (repository.isDisabled(Priority.ERROR_INT))
			return;
		if (Level.ERROR.isGreaterOrEqual(getEffectiveLevel())) {
			forcedLog(FQCN, Level.ERROR, messagelog, null);
		}
	}

	public void error(String module, Object message, Throwable t, Object... params) {
		Object messagelog = decorateMessage(module, message, params);
		if (repository.isDisabled(Priority.ERROR_INT))
			return;
		if (Level.ERROR.isGreaterOrEqual(getEffectiveLevel())) {
			forcedLog(FQCN, Level.ERROR, messagelog, t);
		}
	}

	public void fatal(String module, Object message, Object... params) {
		Object messagelog = decorateMessage(module, message, params);
		if (repository.isDisabled(Priority.FATAL_INT))
			return;
		if (Level.FATAL.isGreaterOrEqual(getEffectiveLevel())) {
			forcedLog(FQCN, Level.FATAL, messagelog, null);
		}
	}

	public void fatal(String module, Object message, Throwable t, Object... params) {
		Object messagelog = decorateMessage(module, message, params);
		if (repository.isDisabled(Priority.FATAL_INT))
			return;
		if (Level.FATAL.isGreaterOrEqual(getEffectiveLevel())) {
			forcedLog(FQCN, Level.FATAL, messagelog, t);
		}
	}

	/**
	 * 
	 */
	private Object decorateMessage(String module, Object message, Object... params) {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(module).append("] ").append(message);
		try {
			if (params != null && params.length > 0) {
				builder.append(" [");
				int length = params.length;
				for (int i = 0; i < length; i++) {
					builder.append(params[i]);
					if (i < length - 1) {
						builder.append(", ");
					}
				}
				builder.append("]");
			}
		} catch (Exception e) {
			builder.append(e);
		}
		return builder;
	}

	/*
	 * 
	 */
	@Override
	protected void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
		if (null == message) {
			return;
		}

		String str = null;
		try {
			if (message instanceof Map || message instanceof List || message instanceof Set) {
				str = JSON.toJSONString(message);
			} else {
				str = message.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		MDC.put("businessId", "");
		super.forcedLog(fqcn, level, str, t);
		MDC.remove("businessId");
		
	}
}
