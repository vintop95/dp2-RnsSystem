package it.polito.dp2.RNS.sol3.service.exceptions;

public class EntranceRefusedRnsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EntranceRefusedRnsException() {
	}

	public EntranceRefusedRnsException(String message) {
		super(message);
	}

	public EntranceRefusedRnsException(Throwable cause) {
		super(cause);
	}

	public EntranceRefusedRnsException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntranceRefusedRnsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}