package org.atlas.gateway.components.mediator;

public class Subscription{

	private String topic;
	private int qos;
	private Bridge bridge;
	
	public Subscription(){
		this.qos = 0;
	}
	
	public Subscription(String topic){
		this.topic = topic;
		this.qos = 0;
	}
	
	public Subscription(Bridge bridge){
		this.bridge = bridge;
		this.topic = this.getBridge().getInputChannel();
		this.qos = 0;
	}
	
	public Subscription(String topic, Bridge bridge){
		this.topic = topic;
		this.bridge = bridge;
		this.qos = 0;
	}
	
	public Subscription(String topic, Bridge bridge, int qos){
		this.topic = topic;
		this.bridge = bridge;
		this.qos = qos;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Bridge getBridge() {
		return bridge;
	}

	public void setBridge(Bridge bridge) {
		this.bridge = bridge;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}
	
}
