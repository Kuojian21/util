package com.tools.quartz.demo;

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

public class Quartz {

	public static class MyJob implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			long index = 0;
			try {
				while (true) {
					System.out.println("MyJob" + index++);
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("########over");
		}

	}
	
	public static void demo() throws SchedulerException, InterruptedException {
		SchedulerFactory factory = new StdSchedulerFactory();
		Scheduler scheduler = factory.getScheduler();
		scheduler.start();
		JobDetail jobDetail = JobBuilder.newJob(MyJob.class).build();
		MutableTrigger trigger = CronScheduleBuilder.cronSchedule("0 49 * * * ?").build();
		trigger.setKey(new TriggerKey("default"));
		scheduler.scheduleJob(jobDetail, trigger);
//		Thread.sleep(120000);
//		scheduler.shutdown();
	}
	

	public static void main(String[] args) throws SchedulerException, InterruptedException{
		demo();
	}
}


