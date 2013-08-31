package org.adp4j.core;

public final class BeforePollingCycleEvent extends Event{

	BeforePollingCycleEvent(DirectoryPoller dp) {
		super(dp);
	}

}
