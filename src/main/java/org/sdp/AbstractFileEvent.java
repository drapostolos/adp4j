package org.sdp;

import org.sdp.spi.FileObject;

abstract class AbstractFileEvent extends Event{
	private final FileObject file;

	AbstractFileEvent(DirectoryPoller directoryPoller, FileObject file) {
		super(directoryPoller);
		this.file = file;
	}
	
	public FileObject getFile() {
		return file;
	}

}
