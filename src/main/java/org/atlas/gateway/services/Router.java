package org.atlas.gateway.services;

import org.atlas.gateway.components.database.models.Route;

public interface Router {

	public Route getDestination(String source);
	
}
