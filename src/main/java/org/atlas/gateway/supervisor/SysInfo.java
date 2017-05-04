package org.atlas.gateway.supervisor;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SysInfo {
	
	@JsonProperty("os_name")
	private String osName;
	
	@JsonProperty("os_version")
	private String osVersion;
	
	@JsonProperty("os_arch")
	private String osArch;
	private int cores;
	
	@JsonProperty("local_time")
	private String localTime;
	
	@JsonProperty("up_time")
	private long upTime;//In Seconds
	
	@JsonProperty("system_load")
	private SystemLoad sysLoad;
	
	@JsonProperty("tasks_info")
	private SysTasks tasksInfo;
	
	@JsonProperty("cpus_info")
	private SysCpus cpusInfo;
	
	@JsonProperty("ip_addresses")
	private ArrayList<String> ipAddrs; 
	
	@JsonProperty("public_ip")
	private String publicIp;
	
	public SysInfo(){}
	
	public String getOsName() {
		return osName;
	}
	public void setOsName(String osName) {
		this.osName = osName;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public String getOsArch() {
		return osArch;
	}
	public void setOsArch(String osArch) {
		this.osArch = osArch;
	}
	public int getCores() {
		return cores;
	}
	public void setCores(int cores) {
		this.cores = cores;
	}

	public String getLocalTime() {
		return localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}

	public long getUpTime() {
		return upTime;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public SystemLoad getSysLoad() {
		return sysLoad;
	}

	public void setSysLoad(SystemLoad sysLoad) {
		this.sysLoad = sysLoad;
	}

	public SysTasks getTasksInfo() {
		return tasksInfo;
	}

	public void setTasksInfo(SysTasks tasksInfo) {
		this.tasksInfo = tasksInfo;
	}

	public SysCpus getCpusInfo() {
		return cpusInfo;
	}

	public void setCpusInfo(SysCpus cpusInfo) {
		this.cpusInfo = cpusInfo;
	}

	public ArrayList<String> getIpAddrs() {
		return ipAddrs;
	}

	public void setIpAddrs(ArrayList<String> ipAddrs) {
		this.ipAddrs = ipAddrs;
	}

	public String getPublicIp() {
		return publicIp;
	}

	public void setPublicIp(String publicIp) {
		this.publicIp = publicIp;
	}
	
}
