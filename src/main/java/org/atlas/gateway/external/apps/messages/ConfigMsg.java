package org.atlas.gateway.external.apps.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigMsg {

	private boolean found;
	private String application;
	
	@JsonProperty("response_topic")
	private String responseTopic;
	private String config;
	private String configdata;
	
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	public String getConfigdata() {
		return configdata;
	}
	public void setConfigdata(String configdata) {
		this.configdata = configdata;
	}
	public boolean isFound() {
		return found;
	}
	public void setFound(boolean found) {
		this.found = found;
	}
	public String getResponseTopic() {
		return responseTopic;
	}
	public void setResponseTopic(String responseTopic) {
		this.responseTopic = responseTopic;
	}
	
	
	
}
