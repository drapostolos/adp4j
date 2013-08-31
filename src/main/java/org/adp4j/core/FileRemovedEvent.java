package org.adp4j.core;

import org.adp4j.spi.FileObject;
import org.adp4j.spi.PolledDirectory;

public class FileRemovedEvent extends AbstractFileEvent {

	FileRemovedEvent(DirectoryPoller directoryPoller, PolledDirectory directory, FileObject file) {
		super(directoryPoller, directory, file);
	}


}
