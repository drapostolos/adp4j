package org.adp4j.core;

import java.util.HashSet;
import java.util.Set;

import org.adp4j.spi.FileObject;
import org.adp4j.spi.PolledDirectory;

public final class InitialContentEvent extends AbstractDirectoryEvent{
	private Set<FileObject> files;

	InitialContentEvent(DirectoryPoller dp, PolledDirectory directory, Set<FileObject> files) {
		super(dp, directory);
		this.files = files;
	}
	
	public Set<FileObject> getFiles(){
		return new HashSet<FileObject>(files);
	}

}
