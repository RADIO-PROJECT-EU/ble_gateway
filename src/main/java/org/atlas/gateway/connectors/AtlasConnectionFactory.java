package org.atlas.gateway.connectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtlasConnectionFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(AtlasConnectionFactory.class);
	
	public static AtlasConnection createConnection(String host, String identity){
		logger.info("Establishing a new connection to ("+host+") with identity " + identity);
		return new MqttConnection(host, identity);
	}

}
