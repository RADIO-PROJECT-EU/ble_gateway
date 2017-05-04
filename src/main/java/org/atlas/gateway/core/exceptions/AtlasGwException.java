package org.atlas.gateway.core.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtlasGwException extends Exception {
	
	private static final long serialVersionUID = 7468633737373095296L;
	private static final Logger logger = LoggerFactory.getLogger(AtlasGwException.class);

	protected GatewayErrorCode errorCode;

	@SuppressWarnings("unused")
	private AtlasGwException() {
		super();
	}

	/**
	 * Builds a new EdcException instance based on the supplied EdcErrorCode.
	 * 
	 * @param code
	 * @param t
	 * @param arguments
	 */
	public AtlasGwException(GatewayErrorCode code) {
		this.errorCode = code;
	}
	
	public static AtlasGwException internalError(String message) {
		logger.error("Internal Error: " + message);
		return new AtlasGwException(GatewayErrorCode.INTERNAL_ERROR);
	}
	
	public GatewayErrorCode getCode() {
		return this.errorCode;
	}
}
