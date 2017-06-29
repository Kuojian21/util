package com.tools.logger;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class UserLogFactory implements LoggerFactory {

	/* 
	 * @see org.apache.log4j.spi.LoggerFactory#makeNewLoggerInstance(java.lang.String)
	 */
	@Override
	public Logger makeNewLoggerInstance(String name) {
		return new UserLogger(name);
	}

}