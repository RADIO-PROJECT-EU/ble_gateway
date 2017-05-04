package org.atlas.gateway.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WSNConfig {

	@Value("${atlas.gw.wsn.advertisments.adapter:hci0}")
	private String advAdapter;

	public String getAdvAdapter() {
		return advAdapter;
	}

	public void setAdvAdapter(String advAdapter) {
		this.advAdapter = advAdapter;
	}
	
}
