package org.atlas.gateway.components.database.models;

public class Unity {
	
	private String address;
	private int sensorId;
	
	public Unity(){}
	
	public Unity(String address, int sensorId) {
		this.address = address;
		this.sensorId = sensorId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getSensorId() {
		return sensorId;
	}

	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}

}
