package org.atlas.gateway.supervisor;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SysTasks implements TopInfo {
	
	private int total;
	private int running;
	private int sleeping;
	private int stopped;
	private int zombie;
	
	public SysTasks(String info){
		this.parse(info);
	}
	
	private void parse(String info){
		String trimmedInfo = info.trim().replaceAll(" +", " ");
		String[] temp = trimmedInfo.split(" ");
		this.total = Integer.parseInt(temp[1]);
		this.running = Integer.parseInt(temp[3]);
		this.sleeping = Integer.parseInt(temp[5]);
		this.stopped = Integer.parseInt(temp[7]);
		this.zombie = Integer.parseInt(temp[9]);
	}
	
	@Override
	@JsonIgnore
	public InfoType getInfoType() {
		return InfoType.TASKS;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getRunning() {
		return running;
	}

	public void setRunning(int running) {
		this.running = running;
	}

	public int getSleeping() {
		return sleeping;
	}

	public void setSleeping(int sleeping) {
		this.sleeping = sleeping;
	}

	public int getStopped() {
		return stopped;
	}

	public void setStopped(int stopped) {
		this.stopped = stopped;
	}

	public int getZombie() {
		return zombie;
	}

	public void setZombie(int zombie) {
		this.zombie = zombie;
	}

	@Override
	public String toString() {
		return "SysTasks [total=" + total + ", running=" + running + ", sleeping=" + sleeping + ", stopped=" + stopped
				+ ", zombie=" + zombie + "]";
	}

}
