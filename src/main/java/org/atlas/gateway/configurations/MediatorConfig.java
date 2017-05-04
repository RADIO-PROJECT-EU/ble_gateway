package org.atlas.gateway.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MediatorConfig {
	
	@Value("${mediator.local.access.uri}")
	private String localUri;
	
	@Value("${mediator.name}")
	private String name;
	
	@Value("${mediator.memory.usage.limit:67108864}")
	private long memoryUsageLimit;
	
	@Value("${mediator.memory.usage.limit:536870912}")
	private long storeUsageLimit;
	
	@Value("${mediator.memory.usage.limit:134217728}")
	private long tempUsageLimit;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocalUri() {
		return localUri;
	}

	public void setLocalUri(String localUri) {
		this.localUri = localUri;
	}
	
	public long getMemoryUsageLimit() {
		return memoryUsageLimit;
	}

	public void setMemoryUsageLimit(long memoryUsageLimit) {
		this.memoryUsageLimit = memoryUsageLimit;
	}

	public long getStoreUsageLimit() {
		return storeUsageLimit;
	}

	public void setStoreUsageLimit(long storeUsageLimit) {
		this.storeUsageLimit = storeUsageLimit;
	}

	public long getTempUsageLimit() {
		return tempUsageLimit;
	}

	public void setTempUsageLimit(long tempUsageLimit) {
		this.tempUsageLimit = tempUsageLimit;
	}
	
}
