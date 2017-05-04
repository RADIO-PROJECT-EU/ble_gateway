package org.atlas.gateway.connectors;

import org.atlas.gateway.components.mediator.Subscription;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public interface AtlasConnection {
	
	public MqttClient getClient();
	public void publish(String topic, byte[] payload, int qos);
	public void connect(MqttConnectOptions options);
	public void disconnect();
	public boolean checkConnection();
	public void setListener(MqttCallback callback);
	public void addSubscription(Subscription subscription);
	public Subscription getSubscription(String topic);
	public void removeSubscription(Subscription subscription);
	public void removeAllSubscriptions();
	public void setSubscriptions(Subscription[] subscriptions);
	public void reSubscribeTopics();
}
