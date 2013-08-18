package org.sdp;

import org.sdp.spi.FileObject;

public class FileRemovedEvent extends AbstractFileEvent {

	FileRemovedEvent(DirectoryPoller directoryPoller, FileObject file) {
		super(directoryPoller, file);
		// TODO Auto-generated constructor stub
	}


}
