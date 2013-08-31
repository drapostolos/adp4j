package org.adp4j.core;


abstract class Event{
	protected final DirectoryPoller dp;

	Event(DirectoryPoller directoryPoller) {
		dp = directoryPoller;
	}
	
	/**
	 * Returns the {@link DirectoryPoller} instance which fired
	 * this event.
	 */
	public DirectoryPoller getDirectoryPoller(){
		return dp;
	}
	
}
