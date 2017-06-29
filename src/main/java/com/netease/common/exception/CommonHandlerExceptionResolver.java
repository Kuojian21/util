package com.netease.common.exception;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 处理controller异常
 * 
 * @author KevinLiao 2011-12-8
 */
public class CommonHandlerExceptionResolver implements HandlerExceptionResolver {
	
	private static Logger logger = Logger.getLogger(CommonHandlerExceptionResolver.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
	
		logger.error(e.getMessage());
		// TODO 确定报错页面和报错形式
		Map<String,String> model = new HashMap<String,String>();
		model.put("errormsg", e.getMessage());
		return new ModelAndView("error", model);
	}
}
