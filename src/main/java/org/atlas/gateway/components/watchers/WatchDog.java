package org.atlas.gateway.components.watchers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.atlas.gateway.GatewayApplication;
import org.atlas.gateway.core.commands.GwCommandType;
import org.atlas.gateway.core.commands.Operation;
import org.atlas.gateway.services.CloudPublisher;
import org.atlas.gateway.services.MonitoringService;
import org.atlas.gateway.supervisor.Monitoring;
import org.atlas.gateway.utils.OsUtils;
import org.atlas.gateway.utils.OsUtils.OSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WatchDog {
	
	private static final Logger logger = LoggerFactory.getLogger(WatchDog.class);
	private ObjectMapper jsonMapper = new ObjectMapper();
	
	@Autowired
	private MonitoringService resourcesService;
	
	@Autowired
	private CloudPublisher cloudPublisher;
	
	@Scheduled(fixedDelayString = "${atlas.gw.watchdog.monitor.interval}")
	public void checkSystem(){
		logger.debug("Checking system resources");
		Monitoring monitor = resourcesService.checkSystemResources();
		if( monitor == null ) return;
	    try {
	    	this.cloudPublisher.publish("monitoring", this.jsonMapper.writeValueAsString(monitor).getBytes());
		} catch (JsonProcessingException e) {
			logger.error("Unable to publish gateway monitoring information", e);
		}
	}
	
	/**
	 * Apply a command to the application.
	 * @param osoper
	 */
	public void applyCommand(GwCommandType type, Operation osoper){
		
		if( type == GwCommandType.OPERATING_SYSTEM ){
			if( osoper == Operation.REBOOT ){
				try {
					Runtime.getRuntime().exec("shutdown -r now");
				} catch (IOException e) {
					logger.error("Unable to reboot operating system...", e);
				}
			}
		}
		
		if( type == GwCommandType.APPLICATION ){
			if( osoper == Operation.START ){
				
			}else if( osoper == Operation.RESTART ){
				
			}else if( osoper == Operation.SHUTDOWN ){
				
			}
		}
		
	}
	
	
}
