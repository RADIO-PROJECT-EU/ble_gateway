package org.atlas.gateway.components;

import org.atlas.gateway.components.cloud.CloudHandler;
import org.atlas.gateway.components.mediator.Mediator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GatewayFactory {
	
	@Autowired
	private Mediator mediator;

	@Autowired
	private CloudHandler cloudHandler;
	
}
