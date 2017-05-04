package org.atlas.gateway.core.commands;

public enum Operation {
	
	//For Errors
	UNKNOWN,
	
	/*** Applications Operations ***/
	LIST,
	START,
	RESTART,
	SHUTDOWN,
	CHECK,
	
	/*** WSN Devices Operations ***/
	CONNECT,
	CONFIGURE,
	DISCONNECT,

	/*** Operating System Operations ***/
	REBOOT,
	
	/*** Wireless Mode Operations ***/
	CONNECTABLE,
	OBSERVER,
	ADVERTISER;
	
	
	/**
	 * Shutdown the Operating System
	 *
	
	
	/**
	 * Shutdown the application only
	 *
	SHUTDOWN_APP_ONLY,
	
	/**
	 * Reboot the application only.
	 *
	REBOOT_APP_ONLY,
	*/

}
