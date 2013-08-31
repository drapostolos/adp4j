package org.adp4j.core;

import org.adp4j.spi.FileObject;
import org.adp4j.spi.PolledDirectory;

public class FileModifiedEvent extends AbstractFileEvent {

	FileModifiedEvent(DirectoryPoller directoryPoller, PolledDirectory directory, FileObject file) {
		super(directoryPoller, directory, file);
	}

}
