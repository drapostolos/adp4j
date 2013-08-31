package org.adp4j.core;

import org.adp4j.spi.PolledDirectory;

public class IoErrorCeasedEvent extends AbstractDirectoryEvent {

	public IoErrorCeasedEvent(DirectoryPoller dp, PolledDirectory directory) {
		super(dp, directory);
	}

}
