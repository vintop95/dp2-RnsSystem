package it.polito.dp2.RNS.sol3.service.exceptions;

public class SyntaxRnsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SyntaxRnsException() {
	}

	public SyntaxRnsException(String message) {
		super(message);
	}

	public SyntaxRnsException(Throwable cause) {
		super(cause);
	}

	public SyntaxRnsException(String message, Throwable cause) {
		super(message, cause);
	}

	public SyntaxRnsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
