package org.adp4j.core;

/**
 * An event that is triggered before each poll-cycle of the {@link DirectoryPoller}.
 */
public final class BeforePollingCycleEvent extends Event{

	BeforePollingCycleEvent(DirectoryPoller dp) {
		super(dp);
	}

}
