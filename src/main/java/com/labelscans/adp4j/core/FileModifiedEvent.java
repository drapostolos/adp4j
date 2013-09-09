package com.labelscans.adp4j.core;

import com.labelscans.adp4j.spi.FileElement;
import com.labelscans.adp4j.spi.PolledDirectory;

/**
 * An event that represents a modified file in the {@link PolledDirectory}.
 *
 */
public final class FileModifiedEvent extends AbstractFileEvent {

	FileModifiedEvent(DirectoryPoller directoryPoller, PolledDirectory directory, FileElement file) {
		super(directoryPoller, directory, file);
	}

}
