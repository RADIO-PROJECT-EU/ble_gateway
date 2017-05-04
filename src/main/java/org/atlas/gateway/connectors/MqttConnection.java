package org.atlas.gateway.connectors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.atlas.gateway.components.mediator.Subscription;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttConnection implements AtlasConnection{
	
	private static final Logger logger = LoggerFactory.getLogger(MqttConnection.class);
	private MqttClient mqclient;
	private HashMap<String, Subscription> subscriptionList;
	private MqttConnectOptions connectionOptions;
	
	public MqttConnection(String host, String identity) {
		this.subscriptionList = new HashMap<String, Subscription>();
		try {
			this.mqclient = new MqttClient(host, identity, new MemoryPersistence());
			/*ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					logger.info("Checking connection...");
					if( !mqclient.isConnected() ) {
						logger.info("Connection is not established, retrying...");
						connect(connectionOptions);
					}
				}
			}, 5, 15, TimeUnit.SECONDS);*/
			
		} catch (MqttException e) {
			logger.error("Error while trying to connect to MQTT provider: ",e); 
		}
	}

	@Override
	public void publish(String topic, byte[] payload, int qos) {
		try {
			if( this.mqclient.isConnected() ){
				MqttMessage msg = new MqttMessage(payload);
				msg.setQos(qos);
				msg.setRetained(false);
				this.mqclient.publish(topic, msg);
			}else{
				logger.warn("MQTT Client is not connected");
			}
		} catch (MqttPersistenceException e) {
			logger.error("Unable to set persistent functionality to message", e);
		} catch (MqttException e) {
			logger.error("Unable to publish message on topic " + topic, e);
		}
	}

	@Override
	public void connect(MqttConnectOptions options) {
		this.connectionOptions = options;
		try {
			this.mqclient.connect(this.connectionOptions);
			logger.info("Cloud connection ("+this.mqclient.getServerURI()+") established...");
			logger.info("Checking connectivity status: " + this.mqclient.isConnected());
		} catch (MqttException e) {
			logger.error("Unable to connect to MQTT Server("+this.mqclient.getServerURI()+"): ", e);
		}
		
	}

	@Override
	public void disconnect() {
		try {
			this.removeAllSubscriptions();
			this.mqclient.setCallback(null);
			this.mqclient.disconnect();
		} catch (MqttException e) {
			logger.error("Unable to disconnect from MQTT Server("+this.mqclient.getServerURI()+"): ", e);
		}
	}

	@Override
	public void setListener(MqttCallback callback) {
		logger.info("Setting messages listener");
		if( callback != null ){
			this.mqclient.setCallback(callback);
		}
		logger.info("Setting messages listener...OK");
	}

	@Override
	public void addSubscription(Subscription subscription) {
		try {
			this.mqclient.subscribe(subscription.getTopic(),subscription.getQos());
			this.subscriptionList.put(subscription.getTopic(), subscription);
		} catch (MqttException e) {
			logger.error("Unable to subscribe to topic: " + subscription.getTopic(), e);
		}
	}

	@Override
	public void removeSubscription(Subscription subscription) {
		try {
			this.mqclient.unsubscribe(subscription.getTopic());
			this.removeSubscriptionByName(subscription.getTopic());
		} catch (MqttException e) {
			logger.error("Unable to subscribe to topic: " + subscription.getTopic(), e);
		}
	}
	
	private void removeSubscriptionByName(String subscriptionName){
		this.subscriptionList.remove(subscriptionName);
	}

	@Override
	public void removeAllSubscriptions() {
		try {
			
			Iterator<Entry<String, Subscription>> it = this.subscriptionList.entrySet().iterator();
		    while (it.hasNext()) {
		        @SuppressWarnings("rawtypes")
				Map.Entry pair = (Map.Entry)it.next();
		        this.mqclient.unsubscribe(((Subscription) pair.getValue()).getTopic());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		} catch (MqttException e) {
			logger.error("Unable to unsubscribe all topics..", e);
		}
	}

	@Override
	public void setSubscriptions(Subscription[] subscriptions) {
		try {
			for( Subscription subscription : subscriptions ){
				this.mqclient.subscribe(subscription.getTopic());
				this.subscriptionList.put(subscription.getTopic(),subscription);
			}
		} catch (MqttException e) {
			logger.error("Unable to subscribe to topics..", e);
		}
	}

	@Override
	public MqttClient getClient() {
		return this.mqclient;
	}

	@Override
	public Subscription getSubscription(String topic) {
		return this.subscriptionList.get(topic);
	}

	@Override
	public boolean checkConnection() {
		return this.mqclient.isConnected();
	}

	public HashMap<String, Subscription> getSubscriptionList() {
		return subscriptionList;
	}

	@Override
	public void reSubscribeTopics() {
		logger.info("Re-subscribe to topic after re-connect...");
		Iterator<Entry<String, Subscription>> it = this.subscriptionList.entrySet().iterator();
		String topic = null;
	    while (it.hasNext()) {
	        @SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	        try {
	        	topic = ((Subscription) pair.getValue()).getTopic();
	        	logger.info("Going to re-suscribe to topic: " + topic);
				this.mqclient.unsubscribe(topic);
				this.mqclient.subscribe(topic);
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}
