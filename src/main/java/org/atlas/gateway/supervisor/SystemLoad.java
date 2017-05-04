package org.atlas.gateway.supervisor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemLoad {

	
	/**
	 * Indicates the system load for the last 1 minute
	 */
	@JsonProperty("last_minute_load_average")
	private double load1;
	
	/**
	 * Indicates the system load for the last 5 minutes
	 */
	@JsonProperty("last_five_minutes_load_average")
	private double load5;
	
	/**
	 * Indicates the system load for the last 15 minutes
	 */
	@JsonProperty("last_fifteen_minutes_load_average")
	private double load15;
	
	public SystemLoad(){}
	public SystemLoad(double load1, double load5, double load15){
		this.load1 = load1;
		this.load5 = load5;
		this.load15 = load15;
	}

	public double getLoad1() {
		return load1;
	}

	public void setLoad1(double load1) {
		this.load1 = load1;
	}

	public double getLoad5() {
		return load5;
	}

	public void setLoad5(double load5) {
		this.load5 = load5;
	}

	public double getLoad15() {
		return load15;
	}

	public void setLoad15(double load15) {
		this.load15 = load15;
	}
	
}
