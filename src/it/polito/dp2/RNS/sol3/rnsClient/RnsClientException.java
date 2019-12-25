package it.polito.dp2.RNS.sol3.rnsClient;

@SuppressWarnings("serial")
public class RnsClientException extends Exception {
	/**
	 * The underlying cause of this exception, if any.
	 */
	private Exception exception;

	/**
	 * Constructor with no detail message.
	 */
	public RnsClientException() {
		super();
	}

	/**
	 * Constructor with the specified detail message.
	 * 
	 * @param msg the detail message
	 */
	public RnsClientException(String msg) {
		super(msg);
	}

	/**
	 * Constructor with the specified underlying cause.
	 * 
	 * @param e the underlying cause of this exception
	 */
	public RnsClientException(Exception e) {
		super(e);
		exception = e;
	}

	/**
	 * Constructor with the specified underlying cause and detail message.
	 * 
	 * @param e the underlying cause of this exception
	 * @param msg the detail message
	 */
	public RnsClientException(Exception e, String msg) {
		super(msg, e);
		exception = e;
	}

	/**
	 * Returns the message for this exception, if any.
	 */
	public String getMessage() {
		String message = super.getMessage();
		if (message == null && exception != null) {
			message = exception.getMessage();
		}
		return message;
	}

	/**
	 * Returns the underlying cause of this error, if any.
	 * 
	 * @return Returns the underlying cause of this error, if any.
	 */
	public Exception getException() {
		return exception;
	}
}