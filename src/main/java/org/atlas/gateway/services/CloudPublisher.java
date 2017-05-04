package org.atlas.gateway.services;

public interface CloudPublisher {
	
	public boolean publish(String source, byte[] data);

}
