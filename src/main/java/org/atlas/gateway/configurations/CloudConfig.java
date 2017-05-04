package org.atlas.gateway.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudConfig {
	
	@Value("${atlas.cloud.mqtt.host}")
	private String host;
	
	@Value("${atlas.cloud.mqtt.username}")
	private String username;
	
	@Value("${atlas.cloud.mqtt.password}")
	private String password;
	
	@Value("${atlas.cloud.mqtt.authentication.enabled}")
	private boolean authEnabled;
	
	@Value("${atlas.cloud.mqtt.will.topic: atlas/gateways/wills}")
	private String willTopic;
	
	@Value("${atlas.cloud.mqtt.will.message: Gateway lost connection}")
	private String willMessage;
	
	public boolean isAuthEnabled() {
		return authEnabled;
	}

	public void setAuthEnabled(boolean authEnabled) {
		this.authEnabled = authEnabled;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getWillTopic() {
		return willTopic;
	}

	public void setWillTopic(String willTopic) {
		this.willTopic = willTopic;
	}

	public String getWillMessage() {
		return willMessage;
	}

	public void setWillMessage(String willMessage) {
		this.willMessage = willMessage;
	}
	
}
