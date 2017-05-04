package org.atlas.gateway.components.scheduler;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.atlas.gateway.components.scheduler.tasks.ScheduleType;
import org.atlas.gateway.components.scheduler.tasks.Task;
import org.atlas.gateway.components.scheduler.tasks.TaskType;
import org.atlas.gateway.configurations.SchedulerConfig;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchedulerComponent {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerComponent.class);
	
	@Autowired
	private SchedulerConfig schedulerConfig;
	private SchedulerFactory schFactory;
	private Scheduler scheduler;
	
	//Define the repeating Jobs.
	private HashMap<String, JobKey> repeatingJobs;
	
	@PostConstruct
	public void bootUpScheduler(){
		logger.info("Booting up Scheduler");
		this.repeatingJobs = new HashMap<String, JobKey>();
		this.schFactory = new StdSchedulerFactory();
		try {
			this.scheduler = this.schFactory.getScheduler();
			this.scheduler.start();
			if( this.scheduler.isStarted() ){
				logger.info("Scheduler started successfullly...");
			}
		} catch (SchedulerException e) {
			logger.error("Unable to initialize Scheduler", e);
		}
		logger.info("Initialize task Queues Threads to " + this.schedulerConfig.getQueueThreads());
		logger.info("Scheduler booted successfullly...");
	}
	
	@PreDestroy
	public void shutdownScheduler(){
		try {
			this.scheduler.shutdown();
		} catch (SchedulerException e) {
			logger.error("Unable to shutdown Scheduler", e);
		}
	}
	
	/***
	 * Add a new task to scheduler
	 */
	public boolean submitTask(Task task){
		logger.info("New task submitted with identifier: "+task.getIdentifier());
		return this.examineTask(task);
	}
	
	/**
	 * Remove task from scheduler
	 */
	public boolean removeTask(String identity){
		try {
			JobKey key = this.repeatingJobs.get(identity);
			if( key == null ){
				logger.warn("Unable to find key with identity: " + identity);
				return false;
			}
			boolean status = this.scheduler.deleteJob(key);
			if( status ){
				logger.info("Task("+identity+") removed successfully...");
				System.out.println(this.repeatingJobs.size());
				this.repeatingJobs.remove(identity);
				System.out.println(this.repeatingJobs.size());
			}else{
				logger.warn("Unable to remote task...");
			}
			return status;
		} catch (SchedulerException e) {
			logger.error("Unable to remove task from scheduler",e);
		}
		return false;
	}
	
	/**
	 * Remove task from scheduler
	 */
	public boolean updateTask(String identity, Task task){
		this.removeTask(identity);
		task.setIdentifier(identity);
		return this.submitTask(task);
	}
	
	/**
	 * Examinig a new task before add it to the Scheduler
	 * @param task
	 * @return
	 */
	private boolean examineTask(Task task){
		if( this.isTaskValid(task) ) {
			logger.info("Examining submitted task Type("+task.getType()+"), Schedule("+task.getSchedule()+")" );
			JobDetail jobDetail = JobBuilder.newJob(Task.class).withIdentity(task.getIdentifier()).build();
			Trigger jobTrigger = null;
			if( task.getType() == TaskType.SIMPLE ){
				if( (task.getSchedule() == ScheduleType.NOW) ) {
					jobTrigger = TriggerBuilder.newTrigger().startNow().build(); 
				}else if((task.getSchedule() == ScheduleType.AT)){
					jobTrigger = TriggerBuilder.newTrigger()
						    .startAt(task.getStartAt())
						    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
						    .build();
				}
			}else if( task.getType() == TaskType.REPEAT ){
				jobTrigger = TriggerBuilder.newTrigger()
					    	.startAt(task.getStartAt())
					    	.withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(task.getRepeatCounter()).withIntervalInSeconds(task.getRepeatInterval()))
					    	.build();
				this.repeatingJobs.put(task.getIdentifier(), jobDetail.getKey());
			}
			
			try {
				jobDetail.getJobDataMap().put("executable", task.getExecutable());
				this.scheduler.scheduleJob(jobDetail, jobTrigger);
				logger.info("New task with identifier: "+task.getIdentifier() + ", submitted successfully!!!");
				return true;
			} catch (SchedulerException e) {
				logger.error("Unable to submit task to scheduler",e);
			}
		}
		logger.error("Task examination failed for task, missing attributes, check that identifier,type and schedule are setted successfully");
		return false;
	}
	
	/**
	 * Checking the validity of the task
	 * @param task
	 * @return
	 */
	private boolean isTaskValid(Task task){
		if( task.getIdentifier() == null || task.getType() == null || task.getSchedule() == null ) return false;
		if( task.getType() == TaskType.SIMPLE && task.getSchedule() == ScheduleType.AT ){
			if( task.getStartAt() == null ) return false;
		}
		return true;
	}
	
}
