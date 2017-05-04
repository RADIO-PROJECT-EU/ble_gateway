package org.atlas.gateway.supervisor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SysGeneral implements TopInfo {
	
	private String osTime;
	
	/**
	 *	The system uptime in seconds 
	 */
	@JsonProperty("up_time")
	private long upTime;
	
	@JsonProperty("connected_users")
	private int connectedUsers;
	
	@JsonProperty("last_minute_load_average")
	private double lastMinuteLoadAverage;
	
	@JsonProperty("last_five_minutes_load_average")
	private double lastFiveMinutesLoadAverage;
	
	@JsonProperty("last_fifteen_minutes_load_average")
	private double lastFifteenMinutesLoadAverage;
	
	public SysGeneral(String info){
		this.parse(info);
	}
	
	private void parse(String info){
		String trimmedInfo = info.trim().replaceAll(" +", " ");
		String[] temp = trimmedInfo.split(" ");
		this.osTime = temp[2];
		this.upTime = this.examineUptime(trimmedInfo);
		this.connectedUsers = Integer.parseInt(temp[(temp.length-7)]);
		String avgTemp = "";
		avgTemp = temp[(temp.length-3)].replaceAll(",+", ".");
		this.lastMinuteLoadAverage = Double.parseDouble(avgTemp.substring(0, (avgTemp.length()-1)));
		avgTemp = temp[(temp.length-2)].replaceAll(",+", ".");
		this.lastFiveMinutesLoadAverage = Double.parseDouble(avgTemp.substring(0, (avgTemp.length()-1)));
		avgTemp = temp[(temp.length-1)].replaceAll(",+", ".");
		this.lastFifteenMinutesLoadAverage = Double.parseDouble(avgTemp);
	}

	public String getOsTime() {
		return osTime;
	}

	public void setOsTime(String osTime) {
		this.osTime = osTime;
	}

	public long getUpTime() {
		return upTime;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public int getConnectedUsers() {
		return connectedUsers;
	}

	public void setConnectedUsers(int connectedUsers) {
		this.connectedUsers = connectedUsers;
	}

	public double getLastMinuteLoadAverage() {
		return lastMinuteLoadAverage;
	}

	public void setLastMinuteLoadAverage(double lastMinuteLoadAverage) {
		this.lastMinuteLoadAverage = lastMinuteLoadAverage;
	}

	public double getLastFiveMinutesLoadAverage() {
		return lastFiveMinutesLoadAverage;
	}

	public void setLastFiveMinutesLoadAverage(double lastFiveMinutesLoadAverage) {
		this.lastFiveMinutesLoadAverage = lastFiveMinutesLoadAverage;
	}

	public double getLastFifteenMinutesLoadAverage() {
		return lastFifteenMinutesLoadAverage;
	}

	public void setLastFifteenMinutesLoadAverage(double lastFifteenMinutesLoadAverage) {
		this.lastFifteenMinutesLoadAverage = lastFifteenMinutesLoadAverage;
	}

	@Override
	public String toString() {
		return "SysGeneral [osTime=" + osTime + ", upTime=" + upTime + ", connectedUsers=" + connectedUsers
				+ ", lastMinuteLoadAverage=" + lastMinuteLoadAverage + ", lastFiveMinutesLoadAverage="
				+ lastFiveMinutesLoadAverage + ", lastFifteenMinutesLoadAverage=" + lastFifteenMinutesLoadAverage + "]";
	}

	@Override
	public InfoType getInfoType() {
		return InfoType.GENERAL;
	}
	
	private long examineUptime(String row){
		String upTimeIn = "hours";
		long upTimeInLong = 0;
		if( row.contains("min") ){upTimeIn = "minutes";}
		else if( row.contains("days") || row.contains("day") ){upTimeIn = "days";}
		String[] temp = row.split(" ");
		
		if(upTimeIn.equals("minutes")){
			int n4 = Integer.parseInt(temp[4]);
			upTimeInLong = n4*60;
		}else if( upTimeIn.equals("hours") ){
			String[] hourTemp = temp[4].substring(0,(temp[4].length()-1)).split(":");
			int h1 = Integer.parseInt(hourTemp[0]);
			int m1 = Integer.parseInt(hourTemp[1]);
			upTimeInLong = (h1*3600) + (m1*60);
		}else if( upTimeIn.equals("days") ){
			int d1 = Integer.parseInt(temp[4]);
			String[] hourTemp = temp[6].split(":");
			int h1 = Integer.parseInt(hourTemp[0]);
			int m1 = Integer.parseInt(hourTemp[1].substring(0,(hourTemp[1].length()-1)));
			upTimeInLong = (d1*86400) + (h1*3600) + (m1*60);
		}
		return upTimeInLong;
	}

}
