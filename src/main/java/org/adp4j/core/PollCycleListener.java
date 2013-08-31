package org.adp4j.core;

public interface PollCycleListener extends Listener{
	void beforePollingCycle(BeforePollingCycleEvent event);
	void afterPollingCycle(AfterPollingCycleEvent event);

}
