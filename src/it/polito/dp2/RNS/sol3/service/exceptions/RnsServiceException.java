package it.polito.dp2.RNS.sol3.service.exceptions;

/**
 * Generic exception thrown when having an internal 
 * RnsService error
 */
public class RnsServiceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RnsServiceException() {
	}

	public RnsServiceException(String message) {
		super(message);
	}

	public RnsServiceException(Throwable cause) {
		super(cause);
	}

	public RnsServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public RnsServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}