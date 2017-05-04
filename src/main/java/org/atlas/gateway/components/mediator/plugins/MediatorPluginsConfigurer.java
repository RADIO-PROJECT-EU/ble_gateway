package org.atlas.gateway.components.mediator.plugins;

import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediatorPluginsConfigurer {
	
	private ArrayList<BrokerPlugin> plugins;
	private static final Logger logger = LoggerFactory.getLogger(MediatorPluginsConfigurer.class);
	
	public MediatorPluginsConfigurer(){
		this.plugins = new ArrayList<BrokerPlugin>();
		this.initializeAuthentication();
	}

	private void initializeAuthentication() {
		logger.info("Setting up mediator authentication...");
		List<AuthenticationUser> users = new ArrayList<AuthenticationUser>();
		users.add(new AuthenticationUser("<>", "<>", "apps"));
		SimpleAuthenticationPlugin authenticationPlugin = new SimpleAuthenticationPlugin(users);
		authenticationPlugin.setAnonymousAccessAllowed(false);	
		this.plugins.add(authenticationPlugin);
	}

	public BrokerPlugin[] getBrokerPlugins() {
		BrokerPlugin[] array = new BrokerPlugin[this.plugins.size()];
		return this.plugins.toArray(array);
	}
	
	

}
