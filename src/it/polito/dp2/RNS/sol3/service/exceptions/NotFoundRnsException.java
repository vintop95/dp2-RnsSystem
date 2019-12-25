package it.polito.dp2.RNS.sol3.service.exceptions;

public class NotFoundRnsException extends Exception {
	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	
	public NotFoundRnsException() {
	}

	public NotFoundRnsException(String message) {
		super(message);
	}

	public NotFoundRnsException(Throwable cause) {
		super(cause);
	}

	public NotFoundRnsException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundRnsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
