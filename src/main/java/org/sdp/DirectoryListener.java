package org.sdp;

public interface DirectoryListener extends Listener{
	void fileAdded(FileAddedEvent event);
	void fileRemoved(FileRemovedEvent event);
	void fileModified(FileModifiedEvent event);
}
