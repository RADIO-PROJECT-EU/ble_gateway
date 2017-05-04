package org.atlas.gateway.components.mediator;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.store.kahadb.KahaDBStore;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.usage.StoreUsage;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.usage.TempUsage;
import org.atlas.gateway.components.mediator.plugins.MediatorPluginsConfigurer;
import org.atlas.gateway.configurations.MediatorConfig;
import org.atlas.gateway.utils.OsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Mediator {
	
	private static final Logger logger = LoggerFactory.getLogger(Mediator.class);
	
	@Autowired
	private MediatorConfig config;
	
	private BrokerService mediator;
	
	@PostConstruct
	public void bootUpMediator(){
		logger.info("Booting up Mediator...");
		
		this.mediator = new BrokerService();
		this.mediator.setBrokerName(config.getName());
		this.mediator.setPersistent(true);
		
		logger.info("Configuring system memory limits");
		this.mediator.setSystemUsage(this.configureSystemUsage());	
		
		if( this.mediator.isPersistent() ){
			logger.info("Enabling mediator persistence functionality");
			try {
				this.mediator.setPersistenceAdapter(this.getPersistentAdapter());
			} catch (IOException e) {
				logger.error("Unable to enable mediator persistence functionality",e);
			}
		}
		
		try {
			logger.info("Loading Broker plugins");
			this.mediator.setPlugins(new MediatorPluginsConfigurer().getBrokerPlugins());
			logger.info("Broker plugins ("+this.mediator.getPlugins().length+") loaded successfully....");
			
			this.mediator.addConnector(this.getTransportConnector());
			this.mediator.start();
			
		} catch (Exception e) {
			logger.error("Unable to initialize Mediator connections",e);
		}
		logger.info("Mediator booted successfullly...");
	}
	
	@PreDestroy
	public void shutdownMediator(){
		try {
			this.mediator.stop();
		} catch (Exception e) {
			logger.error("Unable to stop Mediator...",e);
		}
	}
	
	private TransportConnector getTransportConnector() throws Exception{
		TransportConnector mqtt = new TransportConnector();
		mqtt.setUri(new URI(this.config.getLocalUri()));
		return mqtt;
	}
	
	private KahaDBStore getPersistentAdapter(){
		KahaDBStore kaha = new KahaDBStore();
		kaha.setDirectory(new File(OsUtils.getEnviromentalVariable("GATEWAY_HOME") + "/kaha/atlasdb"));
        kaha.setConcurrentStoreAndDispatchQueues(true);
        kaha.setConcurrentStoreAndDispatchTopics(false);
		return kaha;
	}
	
	private SystemUsage configureSystemUsage(){
		SystemUsage memoryManager = new SystemUsage();		
		
		MemoryUsage memoryUsage = new MemoryUsage();
		memoryUsage.setLimit(config.getMemoryUsageLimit());
		memoryManager.setMemoryUsage(memoryUsage);
		
		StoreUsage storeUsage = new StoreUsage();
		storeUsage.setLimit(config.getStoreUsageLimit());
		memoryManager.setStoreUsage(storeUsage);
		
		TempUsage tempUsage = new TempUsage();
		tempUsage.setLimit(config.getTempUsageLimit());
		memoryManager.setTempUsage(tempUsage);
		
		return memoryManager;
	}

}

