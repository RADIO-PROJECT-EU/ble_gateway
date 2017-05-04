package org.atlas.gateway.core.commands;

public enum GwCommandType {
	
	//For Errors
	UNKNOWN,
	
	/**
	 * Wireless Mode
	 */
	WIRELESS_MODE,
	
	/**
	 * Perform an action to a device
	 */
	DEVICE,
	
	/**
	 * Ping a device
	 */
	PING,
	
	/**
	 * Send an advertisment
	 */
	ADVERTISEMENT,
	
	/**
	 * Check battery
	 */
	BATTERY_INDICATION,
	
	/**
	 * Commands that will performed to Operating System
	 */
	OPERATING_SYSTEM,
	
	/**
	 * Check Resources of the System
	 */
	RESOURCES,
	
	/**
	 * Command for the Application
	 */
	APPLICATION;

}
