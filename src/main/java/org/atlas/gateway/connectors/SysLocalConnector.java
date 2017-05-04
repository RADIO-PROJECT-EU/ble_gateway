package org.atlas.gateway.connectors;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class SysLocalConnector {

	private static AtlasConnection localConnection;
	
	static{
		localConnection = AtlasConnectionFactory.createConnection("tcp://127.0.0.1:1883", "Local-Client-Publisher");
		localConnection.setListener(null);
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName("application");
		options.setPassword("@PpL3c@tI0n".toCharArray());
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		localConnection.connect(options);
	}

	/**
	 * Publish message to the Applications
	 * @param topic - Topic
	 * @param payload - Payload
	 */
	public static void publish(String topic, byte[] payload, int qos){
		localConnection.publish(topic, payload, qos);
	}
	
}
