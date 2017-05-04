package org.atlas.gateway.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {

	@Value("${scheduler.queue.threads:5}")
	private int queueThreads;

	public int getQueueThreads() {
		return queueThreads;
	}

	public void setQueueThreads(int queueThreads) {
		this.queueThreads = queueThreads;
	}
	
}
