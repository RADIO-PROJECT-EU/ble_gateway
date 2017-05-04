package org.atlas.gateway.components.mediator;

import org.atlas.gateway.components.mediator.processors.Processor;
import org.atlas.gateway.components.mediator.transformers.Transform;

public class Bridge {

	private String inputChannel;
	private String outputChannel;
	private BridgeType type;
	private Transform transformer;
	private Processor processor;
	
	public String getInputChannel() {
		return inputChannel;
	}
	public void setInputChannel(String inputChannel) {
		this.inputChannel = inputChannel;
	}
	public String getOutputChannel() {
		return outputChannel;
	}
	public void setOutputChannel(String outputChannel) {
		this.outputChannel = outputChannel;
	}
	public Transform getTransformer() {
		return transformer;
	}
	public void setTransformer(Transform transformer) {
		this.transformer = transformer;
	}
	public BridgeType getType() {
		return type;
	}
	public void setType(BridgeType type) {
		this.type = type;
	}
	public Processor getProcessor() {
		return processor;
	}
	public void setProcessor(Processor processor) {
		this.processor = processor;
	}
	
}
