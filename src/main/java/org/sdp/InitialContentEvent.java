package org.sdp;

import java.util.HashSet;
import java.util.Set;

import org.sdp.spi.FileObject;

public final class InitialContentEvent extends Event{
	private Set<FileObject> files;

	InitialContentEvent(DirectoryPoller dp, Set<FileObject> files) {
		super(dp);
		this.files = files;
	}
	
	public Set<FileObject> getFiles(){
		return new HashSet<FileObject>(files);
	}

}
