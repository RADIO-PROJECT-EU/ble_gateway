package org.atlas.wsn.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsnException extends Exception {
	
	private static final long serialVersionUID = 7468633737373095296L;
	private static final Logger logger = LoggerFactory.getLogger(WsnException.class);

	protected WsnErrorCode errorCode;

	@SuppressWarnings("unused")
	private WsnException() {
		super();
	}

	/**
	 * Builds a new EdcException instance based on the supplied EdcErrorCode.
	 * 
	 * @param code
	 * @param t
	 * @param arguments
	 */
	public WsnException(WsnErrorCode code) {
		this.errorCode = code;
	}
	
	public static WsnException internalError(String message) {
		logger.error("Internal Error: " + message);
		return new WsnException(WsnErrorCode.INTERNAL_ERROR);
	}
	
	public WsnErrorCode getCode() {
		return this.errorCode;
	}
}
