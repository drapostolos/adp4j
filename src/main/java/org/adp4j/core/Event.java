package org.adp4j.core;


abstract class Event{
	protected final DirectoryPoller dp;

	Event(DirectoryPoller directoryPoller) {
		dp = directoryPoller;
	}
	
	public DirectoryPoller getDirectoryPoller(){
		return dp;
	}
	
}
