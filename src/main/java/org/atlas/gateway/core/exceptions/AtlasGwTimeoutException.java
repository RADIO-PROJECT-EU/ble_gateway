package org.atlas.gateway.core.exceptions;

/**
 * AtlasGwTimeoutException is raised when the attempted operation failed to respond before the timeout exprises.
 */
public class AtlasGwTimeoutException extends AtlasGwException {
	
	private static final long serialVersionUID = -3042470573773974746L;

	public AtlasGwTimeoutException(String message) {
		super(GatewayErrorCode.TIMED_OUT);
		System.out.println(message);//TODO
	}
	
	public AtlasGwTimeoutException(String message, Throwable cause) {
		super(GatewayErrorCode.TIMED_OUT);
		System.out.println(cause.getMessage());//TODO
		System.out.println(message);//TODO
	}
}
