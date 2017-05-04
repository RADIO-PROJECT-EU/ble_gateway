package org.atlas.gateway.components.database.models;

public class Information {
	
	private String configId;
	private String configValue;
	private String createdAt;
	private String updatedAt;
	
	public Information(){}
	
	public Information(String configId, String configValue) {
		this.setConfigId(configId);
		this.setConfigValue(configValue);
	}
	
	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	public String getConfigValue() {
		return configValue;
	}
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
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

}
