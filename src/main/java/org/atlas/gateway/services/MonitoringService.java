package org.atlas.gateway.services;

import java.util.ArrayList;

import org.atlas.gateway.components.database.models.Application;
import org.atlas.gateway.supervisor.Monitoring;

public interface MonitoringService {

	/**
	 * Checking the System Resources
	 * @return
	 */
	public Monitoring checkSystemResources();
	
	/**
	 *	Checking local running applications 
	 **/
	public ArrayList<Application> checkApplications();
	
	/**
	 * Specific application information
	 */
	public Application getApplication(String name);
 	
}
