package org.adp4j.core;

import java.io.IOException;

import org.adp4j.spi.PolledDirectory;

/**
 * An event that represents the occurrence of an I/O error in the {@link PolledDirectory}.
 *
 */
public final class IoErrorRaisedEvent extends AbstractDirectoryEvent {
	private final IOException ioException;

	IoErrorRaisedEvent(DirectoryPoller dp, PolledDirectory directory, IOException e) {
		super(dp, directory);
		ioException = e;
	}
	
	/**
	 * Returns the {@link IOException} that caused this event.
	 */
	public IOException getIoException() {
		return ioException;
	}

}
