package org.adp4j.core;


public interface DirectoryPollerListener extends Listener{
	void beforeStart(BeforeStartEvent event);
	void afterStop(AfterStopEvent event);
	
	void initialContent(InitialContentEvent event);
}
