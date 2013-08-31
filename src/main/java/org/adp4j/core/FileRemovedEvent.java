package org.adp4j.core;

import org.adp4j.spi.FileElement;
import org.adp4j.spi.PolledDirectory;

/**
 * An event that represents a removed file in the {@link PolledDirectory}.
 *
 */
public final class FileRemovedEvent extends AbstractFileEvent {

	FileRemovedEvent(DirectoryPoller directoryPoller, PolledDirectory directory, FileElement file) {
		super(directoryPoller, directory, file);
	}


}
