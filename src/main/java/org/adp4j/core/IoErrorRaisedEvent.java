package org.adp4j.core;

import java.io.IOException;

import org.adp4j.spi.PolledDirectory;

public class IoErrorRaisedEvent extends AbstractDirectoryEvent {
	private final IOException ioException;

	public IoErrorRaisedEvent(DirectoryPoller dp, PolledDirectory directory, IOException e) {
		super(dp, directory);
		ioException = e;
	}
	
	public IOException getIoException() {
		return ioException;
	}

}
