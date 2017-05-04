package org.atlas.gateway.components.scheduler.tasks;

public enum ScheduleType {
	
	/**
	 * Task will not be added to the execution will executed directly
	 */
	NOW,
	
	/**
	 * The execution of this Task will be done at time (Datetime) or After 10 seconds for example.
	 */
	AT,
	
	/**
	 * The task will be execute every, e.g 10 seconds for example
	 */
	EVERY

}
