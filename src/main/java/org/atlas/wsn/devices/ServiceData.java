package org.atlas.wsn.devices;

import java.util.Date;
import java.util.UUID;

public class ServiceData {
	
	private String uuid;
	private Date timestamp;
	private String device;
	private String datatype;
	private double value;
	private String payload;
	
	public ServiceData(){
		this.uuid = UUID.randomUUID().toString();
		this.timestamp = new Date();
	}
	
	public ServiceData(String device, String datatype, double value){
		this.uuid = UUID.randomUUID().toString();
		this.timestamp = new Date();
		this.device = device;
		this.datatype = datatype;
		this.value  = value;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

}
