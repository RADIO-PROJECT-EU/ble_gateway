package org.atlas.gateway.core.commands;

import java.util.ArrayList;

public class CommandResponse {
	
	private String uuid;
	private GwCommandType type;
	private Operation operation;
	private ArrayList<CommandResult> results;
	
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
	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	public ArrayList<CommandResult> getResults() {
		return results;
	}
	public void setResults(ArrayList<CommandResult> results) {
		this.results = results;
	}
	
	public void addResult(CommandResult result){
		if( this.results == null ) this.results = new ArrayList<CommandResult>();
		this.results.add(result);
	}

}
