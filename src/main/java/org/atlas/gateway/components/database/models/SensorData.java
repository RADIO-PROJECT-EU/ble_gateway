package org.atlas.gateway.components.database.models;

public class SensorData {

	private int rowId;
	private String sensorAddress;
	private String createdAt;
	private String source;
	private  byte[] data;
	private int status;
	
	public String getSensorAddress() {
		return sensorAddress;
	}
	public void setSensorAddress(String sensorAddress) {
		this.sensorAddress = sensorAddress;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getRowId() {
		return rowId;
	}
	public void setRowId(int rowId) {
		this.rowId = rowId;
	}
	
}
