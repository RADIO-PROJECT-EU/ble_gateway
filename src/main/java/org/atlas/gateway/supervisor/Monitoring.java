package org.atlas.gateway.supervisor;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Monitoring {
	
	/**
	 * General System Information
	 */
	@JsonProperty("system_info")
	private SysInfo sysInfo;
	
	/**
	 * The System partitions for the monitoring.
	 */
	@JsonProperty("system_partitions")
	private ArrayList<Partition> partitions;
	
	/**
	 * The System memories
	 */
	@JsonProperty("system_memories")
	private ArrayList<SysMemory> memories;
	
	public Monitoring(){
		this.partitions = new ArrayList<Partition>();
		this.memories = new ArrayList<SysMemory>();
	}

	public ArrayList<Partition> getPartitions() {
		return partitions;
	}

	public void setPartitions(ArrayList<Partition> partitions) {
		this.partitions = partitions;
	}
	
	public void addPartition(Partition partition){
		this.partitions.add(partition);
	}

	public ArrayList<SysMemory> getMemories() {
		return memories;
	}

	public void setMemories(ArrayList<SysMemory> memories) {
		this.memories = memories;
	}
	
	public void addMemory(SysMemory memory){
		this.memories.add(memory);
	}

	public SysInfo getSysInfo() {
		return sysInfo;
	}

	public void setSysInfo(SysInfo sysInfo) {
		this.sysInfo = sysInfo;
	}

}
