package org.sdp;

/**
 * The runtime exception of the Directory Monitor library.
 *
 */
public final class DirectoryPollerException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public DirectoryPollerException() {
		super();
	}

	public DirectoryPollerException(String message) {
		super(message);
	}

	public DirectoryPollerException(Throwable t) {
		super(t);
	}

	public DirectoryPollerException(String message, Throwable t) {
		super(message, t);
	}

}
