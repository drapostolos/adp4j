package org.sdp;


public interface PollerListener extends Listener{
	void beforeStart(BeforeStartEvent event);
	void afterStop(AfterStopEvent event);
	
	void initialContent(InitialContentEvent event);
	
	void beforePollingCycle(BeforePollingCycleEvent event);
	void afterPollingCycle(AfterPollingCycleEvent event);
}
