package it.polito.dp2.RNS.sol3.service.exceptions;

public class UnauthorizedRnsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnauthorizedRnsException() {
	}

	public UnauthorizedRnsException(String message) {
		super(message);
	}

	public UnauthorizedRnsException(Throwable cause) {
		super(cause);
	}

	public UnauthorizedRnsException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthorizedRnsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}