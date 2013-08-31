package org.adp4j.core;

import org.adp4j.spi.FileElement;
import org.adp4j.spi.PolledDirectory;

/**
 * An event that represents a new file added in the {@link PolledDirectory}.
 *
 */
public final class FileAddedEvent extends AbstractFileEvent{

	FileAddedEvent(DirectoryPoller directoryPoller, PolledDirectory directory, FileElement file) {
		super(directoryPoller, directory, file);
	}

}
