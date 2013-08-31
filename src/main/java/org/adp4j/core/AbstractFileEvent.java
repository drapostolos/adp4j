package org.adp4j.core;

import org.adp4j.spi.FileObject;
import org.adp4j.spi.PolledDirectory;

abstract class AbstractFileEvent extends AbstractDirectoryEvent{
	private final FileObject file;

	AbstractFileEvent(DirectoryPoller directoryPoller, PolledDirectory directory, FileObject file) {
		super(directoryPoller, directory);
		this.file = file;
	}
	
	public FileObject getFile() {
		return file;
	}
}
