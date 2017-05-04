package org.atlas.gateway.core.commands;

public class CommandResult {

	private String identity;
	private int signal;
	private int battery;
	private int connected;
	private String ip;
	private int pid;
	private ResultStatus status;
	private String error;
	
	public CommandResult(){}
	
	//Error Constructor
	public CommandResult(ResultStatus status, String reason){
		this.status = status;
		this.error = reason;
	}

	//Error Constructor with Identity
	public CommandResult(String identity, ResultStatus status, String reason){
		this.identity = identity;
		this.status = status;
		this.error = reason;
	}
	
	//Constructor for devices
	public CommandResult(String identity, int signal, int battery, int connected){
		this.status = ResultStatus.SUCCESSFUL;
		this.identity = identity;
		this.signal = signal;
		this.battery = battery;
		this.connected = connected;
	}
	
	//Executed constructor
	public CommandResult(ResultStatus status){
		this.status = status;
	}
	
	//Constructor for Applications Command Result
	public CommandResult(String identity, String ip, int pid){
		this.status = ResultStatus.SUCCESSFUL;
		this.identity = identity;
		this.ip = ip;
		this.pid = pid;
	}
	
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public int getSignal() {
		return signal;
	}
	public void setSignal(int signal) {
		this.signal = signal;
	}
	public int getBattery() {
		return battery;
	}
	public void setBattery(int battery) {
		this.battery = battery;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public ResultStatus getStatus() {
		return status;
	}
	public void setStatus(ResultStatus status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	public int getConnected() {
		return connected;
	}

	public void setConnected(int connected) {
		this.connected = connected;
	}
	
}
