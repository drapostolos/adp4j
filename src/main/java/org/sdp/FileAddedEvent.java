package org.sdp;

import org.sdp.spi.FileObject;

public class FileAddedEvent extends AbstractFileEvent{

	FileAddedEvent(DirectoryPoller directoryPoller, FileObject file) {
		super(directoryPoller, file);
		// TODO Auto-generated constructor stub
	}

}
