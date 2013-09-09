package com.labelscans.adp4j.core;


/**
 * An event that represents the start the {@link DirectoryPoller}, as 
 * triggered by this method: {@link DirectoryPollerBuilder#start()}. 
 */
public final class BeforeStartEvent extends Event {

	BeforeStartEvent(DirectoryPoller dp) {
		super(dp);
	}

}
