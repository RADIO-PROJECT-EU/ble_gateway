package org.atlas.gateway.core.commands;

public enum ResultStatus {

	SUCCESSFUL,
	
	EXECUTED, //No Response available indication, E.g Reboot operation.
	
	TIMEOUT,
	
	ERROR;
	
}
