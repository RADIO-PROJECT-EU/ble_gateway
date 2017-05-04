package org.atlas.wsn.exceptions;

/**
 * AtlasGwTimeoutException is raised when the attempted operation failed to respond before the timeout exprises.
 */
public class WsnTimeoutException extends WsnException {
	
	private static final long serialVersionUID = -3042470573773974746L;

	public WsnTimeoutException(String message) {
		super(WsnErrorCode.TIMED_OUT);
		System.out.println(message);//TODO
	}
	
	public WsnTimeoutException(String message, Throwable cause) {
		super(WsnErrorCode.TIMED_OUT);
		System.out.println(cause.getMessage());//TODO
		System.out.println(message);//TODO
	}
}
