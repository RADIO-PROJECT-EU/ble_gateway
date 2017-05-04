package org.atlas.gateway.supervisor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SysCpus implements TopInfo {
	
	/**
	 * All the above values indicates how the CPU is used.
	 */
	
	/**
	 * Percentage of the CPU for user processes
	 */
	@JsonProperty("user_space")
	private double us;
	
	/**
	 * Percentage of the CPU for system processes
	 */
	@JsonProperty("kernel_space")
	private double sy;
	
	/**
	 * Percentage of the CPU for processes with priority upgrade nice
	 */
	@JsonProperty("low_priority_processes")
	private double ni;
	
	/**
	 * Percentage of the CPU which not used
	 */
	@JsonProperty("idle_operations")
	private double nu;
	
	/**
	 * Percentage of the CPU for processes who waiting for I/O Operations
	 */
	@JsonProperty("io_peripherals_waiting")
	private double wa;
	
	/**
	 * Percentage of the CPU for servicing hardware interrupts
	 */
	@JsonProperty("hardware_interrupts_routines")
	private double hi;
	
	/**
	 * Percentage of the CPU for serving software interrupts
	 */
	@JsonProperty("software_interrupts_routines")
	private double si;
	
	/**
	 * Percentage of the CPU for the hypervisor
	 */
	@JsonProperty("virtual_cpu")
	private double st;
	
	public SysCpus(String info){
		this.parse(info);
	}
	
	private void parse(String info){
		String trimmedInfo = info.trim().replaceAll(" +", " ");
		String[] temp = trimmedInfo.split(" ");
		
		String cpums = "";
		cpums = temp[1].replaceAll(",+", ".");
		this.us = Double.parseDouble(cpums);
		cpums = temp[3].replaceAll(",+", ".");
		this.sy = Double.parseDouble(cpums);
		cpums = temp[5].replaceAll(",+", ".");
		this.ni = Double.parseDouble(cpums);
		cpums = temp[7].replaceAll(",+", ".");
		this.nu = Double.parseDouble(cpums);
		cpums = temp[9].replaceAll(",+", ".");
		this.wa = Double.parseDouble(cpums);
		cpums = temp[11].replaceAll(",+", ".");
		this.hi = Double.parseDouble(cpums);
		cpums = temp[13].replaceAll(",+", ".");
		this.si = Double.parseDouble(cpums);
		cpums = temp[15].replaceAll(",+", ".");
		this.st = Double.parseDouble(cpums);
	}
	
	@Override
	@JsonIgnore
	public InfoType getInfoType() {
		return InfoType.CPUS;
	}

	public double getUs() {
		return us;
	}

	public void setUs(double us) {
		this.us = us;
	}

	public double getSy() {
		return sy;
	}

	public void setSy(double sy) {
		this.sy = sy;
	}

	public double getNi() {
		return ni;
	}

	public void setNi(double ni) {
		this.ni = ni;
	}

	public double getNu() {
		return nu;
	}

	public void setNu(double nu) {
		this.nu = nu;
	}

	public double getWa() {
		return wa;
	}

	public void setWa(double wa) {
		this.wa = wa;
	}

	public double getHi() {
		return hi;
	}

	public void setHi(double hi) {
		this.hi = hi;
	}

	public double getSi() {
		return si;
	}

	public void setSi(double si) {
		this.si = si;
	}

	public double getSt() {
		return st;
	}

	public void setSt(double st) {
		this.st = st;
	}

	@Override
	public String toString() {
		return "SysCpus [us=" + us + ", sy=" + sy + ", ni=" + ni + ", nu=" + nu + ", wa=" + wa + ", hi=" + hi + ", si="
				+ si + ", st=" + st + "]";
	}

}
