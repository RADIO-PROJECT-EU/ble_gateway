package org.atlas.gateway.supervisor;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represent a HDD or SSD Partition
 * @author kasnot
 */
public class Partition {
	
	/**
	 * The name of the partition e.g /dev/sda
	 */
	private String identity;
	
	@JsonProperty("total_space")
	private long totalSpace;
	
	@JsonProperty("free_space")
	private long freeSpace;
	
	@JsonProperty("usable_space")
	private long usableSpace;
	
	public Partition(){}
	
	public Partition(String identity){
		this.identity = identity;
	}
	
	public Partition(String identity, long totalSpace, long freeSpace, long usableSpace){
		this.identity = identity;
		this.totalSpace = totalSpace;
		this.freeSpace = freeSpace;
		this.usableSpace = usableSpace;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public long getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(long totalSpace) {
		this.totalSpace = totalSpace;
	}

	public long getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(long freeSpace) {
		this.freeSpace = freeSpace;
	}

	public long getUsableSpace() {
		return usableSpace;
	}

	public void setUsableSpace(long usableSpace) {
		this.usableSpace = usableSpace;
	}
	

}
