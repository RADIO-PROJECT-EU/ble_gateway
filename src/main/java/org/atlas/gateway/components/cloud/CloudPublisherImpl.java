package org.atlas.gateway.components.cloud;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.atlas.gateway.components.database.models.Route;
import org.atlas.gateway.configurations.GatewayConfig;
import org.atlas.gateway.connectors.AtlasConnection;
import org.atlas.gateway.services.CloudPublisher;
import org.atlas.gateway.services.Router;
import org.atlas.wsn.devices.ServiceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CloudPublisherImpl implements CloudPublisher {
	
	private static final Logger logger = LoggerFactory.getLogger(CloudPublisher.class);
	private ObjectMapper jsonMapper = new ObjectMapper();			
	
	@Autowired
	private GatewayConfig gwConfig;
	
	@Autowired
	private Router router;
	
	@Autowired
	private CloudHandler cloudHandler;
	private AtlasConnection connection;
	
	@PostConstruct
	public void initializeCloudPublisher(){
		this.connection = this.cloudHandler.getCloudConnection();
	}

	@Override
	public boolean publish(String source, byte[] data) {
		Route route = this.router.getDestination(source);
		String destination = route.getDestination();
		int qos = route.getQos();
		destination = destination.replace("{gateway-identity}", this.gwConfig.getIdentity());
		logger.info("Source: "+source+" Destination for the data is: " + destination + " QoS: " + qos);
		if( this.connection == null || !this.connection.checkConnection() ) {
			logger.warn("Connection is not presented, re initializing connection...");
			this.connection = this.cloudHandler.getCloudConnection();
			return false;
		}
		this.connection.publish(destination, data, qos);
		return true;
	}
	
	@Scheduled(fixedDelay=500)
	public void publishData(){
		ArrayList<ServiceData> data = DataPublisher.getData();
		//TODO Dynamic
		
		try {
			for( ServiceData dt: data ){
				this.connection.publish("atlas/notifications", this.jsonMapper.writeValueAsBytes(dt), 1);
			}
		} catch (JsonProcessingException e) {
			logger.error("Unable to construct broadcast message...");
		}
		
		
		
	}

}
