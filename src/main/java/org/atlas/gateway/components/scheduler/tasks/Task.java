package org.atlas.gateway.components.scheduler.tasks;

import java.util.Calendar;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Task implements Job{
	
	private String identifier;
	private TaskType type;
	private ScheduleType schedule;
	private Date startAt;
	private int repeatInterval; //In seconds(Default 10)
	private int repeatCounter;
	private TaskExecutable executable;
	
	public Task(){
		this.startAt = new Date();
		this.repeatInterval = 10;
		this.repeatCounter = -1;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public TaskType getType() {
		return type;
	}
	public void setType(TaskType type) {
		this.type = type;
		if( this.type == TaskType.REPEAT ) this.setSchedule(ScheduleType.EVERY);
	}
	public ScheduleType getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleType schedule) {
		this.schedule = schedule;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			TaskExecutable executor = (TaskExecutable) context.getJobDetail().getJobDataMap().get("executable");
			executor.execute();
        }catch (Exception e) {
        	JobExecutionException jobex = new JobExecutionException(e);
        	jobex.setUnscheduleAllTriggers(true);
        	throw jobex;
        }
		
		
	}

	public Date getStartAt() {
		return startAt;
	}

	public void setStartAt(Date startAt) {
		this.startAt = startAt;
	}
	
	/**
	 * Set the value of start At using seconds from now
	 * @param seconds
	 */
	public void setStartAtBySeconds(int seconds){
		this.setStartAt(this.getFutureDate(seconds));
	}
	
	public int getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(int repeatInterval) {
		this.repeatInterval = repeatInterval;
	}
	
	public int getRepeatCounter() {
		return repeatCounter;
	}

	public void setRepeatCounter(int repeatCounter) {
		//This -1 is because Scheduler start from 0
		this.repeatCounter = (repeatCounter-1);
	}
	
	/**
	 * Calculate a future date for the scheduler
	 */
	private Date getFutureDate(int seconds){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, seconds);
		return cal.getTime();
	}

	public TaskExecutable getExecutable() {
		return executable;
	}

	public void setExecutable(TaskExecutable executable) {
		this.executable = executable;
	}

}
