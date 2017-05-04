package org.atlas.gateway.components.cloud;

import javax.annotation.PostConstruct;

import org.atlas.gateway.components.mediator.Bridge;
import org.atlas.gateway.components.mediator.BridgeType;
import org.atlas.gateway.components.mediator.Subscription;
import org.atlas.gateway.configurations.CloudConfig;
import org.atlas.gateway.configurations.GatewayConfig;
import org.atlas.gateway.connectors.AtlasConnection;
import org.atlas.gateway.connectors.AtlasConnectionFactory;
import org.atlas.gateway.core.commands.CommandProcessor;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CloudHandler implements MqttCallback{
	
	private static final Logger logger = LoggerFactory.getLogger(CloudHandler.class);
	private final int TOTAL_CONNECT_RETRIES_BEFORE_CONNECT = 25;
	private int cloudConnectionFetchCounter = 0;
	private boolean disconnectEventOccured = false;
	
	@Autowired
	private GatewayConfig gwConfig;
	
	@Autowired
	private CloudConfig cloudConfig;
	
	@Autowired
	private CommandProcessor commandProcessor;
	private AtlasConnection connection;
		
	@PostConstruct
	public void bootUpCloudHandler(){
		logger.info("Booting up Cloud Handler...");
		logger.info("Connecting to ATLAS");
		this.connection = AtlasConnectionFactory.createConnection(cloudConfig.getHost(), gwConfig.getIdentity());
		this.connection.setListener(this);
		this.connection.connect(this.getConnectionOptions());		
		if( this.connection.getClient().isConnected() ) {
			logger.info("Initialize Subscriptions");
			connection.addSubscription(new Subscription(this.initializeCommandBridge()));
			logger.info("Connecting to ATLAS...OK");
		}
		logger.info("Cloud Handler booted up successfully...");
	}
	
	private Bridge initializeCommandBridge(){
		Bridge commandBridge = new Bridge();
		commandBridge.setInputChannel(gwConfig.getCommandsTopic());
		commandBridge.setOutputChannel(null);
		commandBridge.setType(BridgeType.CLASS);
		commandBridge.setTransformer(null);
		commandBridge.setProcessor(this.commandProcessor);
		return commandBridge;
	}

	@Override
	public void connectionLost(Throwable cause) {
		this.disconnectEventOccured = true;
		logger.error("Connection lost",cause);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {	
		logger.info("New message arrived on topic: " + topic + ", Checking bridge.");
		Subscription currentSub = this.connection.getSubscription(topic);
		if( currentSub.getBridge() != null ){
			logger.info("Bridge found...");
			if( currentSub.getBridge().getProcessor() != null){
				currentSub.getBridge().getProcessor().processMessage(message.getPayload());
			}
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {}
	
	public AtlasConnection getCloudConnection(){
		if( this.connection != null && this.connection.getClient() != null ){
			this.cloudConnectionFetchCounter++;
			if( this.cloudConnectionFetchCounter >= this.TOTAL_CONNECT_RETRIES_BEFORE_CONNECT ){
				this.cloudConnectionFetchCounter = 0;
				if( !this.connection.getClient().isConnected() )
					this.connection.connect(this.getConnectionOptions());
			}
		}
		return this.connection; 
	}
	
	private MqttConnectOptions getConnectionOptions(){
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(cloudConfig.getUsername());
		options.setPassword(cloudConfig.getPassword().toCharArray());
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setKeepAliveInterval(10);
		options.setWill(cloudConfig.getWillTopic(), cloudConfig.getWillMessage().getBytes(), 0, false);
		return options;
	}
	
	@Scheduled(fixedDelay=15000)
	public void cloudWatcher(){
		if(this.disconnectEventOccured){
			logger.info("Disconnect event occured, checking if connection re-established..");
			if( this.connection.getClient().isConnected() ) {
				this.disconnectEventOccured = false;
				logger.info("Connection re-established. Trying to re-initialize subscriptions");
				this.connection.reSubscribeTopics();
			}
		}
	}
	
}
