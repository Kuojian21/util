package com.tools.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzTool {

	private static final SchedulerFactory factory = new StdSchedulerFactory();

	public static Scheduler scheduler() {
		try {
			Scheduler scheduler = factory.getScheduler();
			if (!scheduler.isStarted()) {
				scheduler.start();
			}
			return scheduler;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void addJob(){
//		scheduler().scheduleJob(jobDetail, trigger);
	}
	
	public static void deleteJob(){
//		scheduler().deleteJob(jobKey);
	}

}
