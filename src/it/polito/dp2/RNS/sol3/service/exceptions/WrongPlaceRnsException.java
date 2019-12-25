package it.polito.dp2.RNS.sol3.service.exceptions;

public class WrongPlaceRnsException  extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongPlaceRnsException() {
	}

	public WrongPlaceRnsException(String message) {
		super(message);
	}

	public WrongPlaceRnsException(Throwable cause) {
		super(cause);
	}

	public WrongPlaceRnsException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongPlaceRnsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}