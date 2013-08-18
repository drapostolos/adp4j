package org.sdp;


abstract class Event{
	protected final DirectoryPoller dp;

	Event(DirectoryPoller directoryPoller) {
		dp = directoryPoller;
	}
	
	public DirectoryPoller getDirectoryPoller(){
		return dp;
	}
	
}
