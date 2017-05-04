package org.atlas.gateway.components.database.models;

public class DeviceDbModel {
	
	private String address;
	private String createdAt;
	private String updatedAt;
	private String wireless_technology; // E.g BLE, Bluetooth, Zigbee
	private int battery_level; // 75 - (%)
	private int connection_type; // Connectable (1), Non-Connectable(0)
	private int status; //(Operational) - Non Operational
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getWireless_technology() {
		return wireless_technology;
	}
	public void setWireless_technology(String wireless_technology) {
		this.wireless_technology = wireless_technology;
	}
	public int getBattery_level() {
		return battery_level;
	}
	public void setBattery_level(int battery_level) {
		this.battery_level = battery_level;
	}
	public int getConnection_type() {
		return connection_type;
	}
	public void setConnection_type(int connection_type) {
		this.connection_type = connection_type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "DeviceDbModel [address=" + address + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
				+ ", wireless_technology=" + wireless_technology + ", battery_level=" + battery_level
				+ ", connection_type=" + connection_type + ", status=" + status + "]";
	}

}
