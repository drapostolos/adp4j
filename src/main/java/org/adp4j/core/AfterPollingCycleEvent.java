package org.adp4j.core;

/**
 * An event that is triggered after each poll-cycle of the {@link DirectoryPoller}.
 */
public final class AfterPollingCycleEvent extends Event{

	AfterPollingCycleEvent(DirectoryPoller dp) {
		super(dp);
	}

}
