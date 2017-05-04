package org.atlas.gateway.core.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GwCommand {

	/**
	 * The identifier for the command
	 */
	private String uuid;
	
	/**
	 * The command type to be executed.
	 */
	private GwCommandType type;
	
	/**
	 * The list of the identities(Can be application name, devices) to perform the command
	 */
	private String[] identities;
	
	/**
	 * For the Command Type ADVERTISMENT, the data to be send to the advertisment.
	 */
	@JsonProperty("advertisement")
	private GwAdvertisment advertisement;
	
	private Operation operation;
	
	/**
	 * Indicates how many seconds will waiting for response for the ping Command.
	 */
	private long timeout;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public GwCommandType getType() {
		return type;
	}

	public void setType(GwCommandType type) {
		this.type = type;
	}

	public String[] getIdentities() {
		return identities;
	}

	public void setIdentities(String[] identities) {
		this.identities = identities;
	}

	public GwAdvertisment getAdvertisement() {
		return advertisement;
	}

	public void setAdvertisement(GwAdvertisment advertisement) {
		this.advertisement = advertisement;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	
}
