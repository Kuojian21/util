package com.tools.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.MutableTrigger;

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

	public static void main(String[] args) throws SchedulerException, InterruptedException{
		Scheduler scheduler = factory.getScheduler();
		scheduler.start();
		JobDetail job = JobBuilder.newJob(MyJob.class).build();
		MutableTrigger trigger = CronScheduleBuilder.cronSchedule("45 46 * * * ?").build();
		trigger.setKey(new TriggerKey("default"));
		scheduler.scheduleJob(job, trigger);
		Thread.sleep(120000);
		scheduler.shutdown();
		System.out.println("@@@@@@@@@shutdown");
	}
}


