package com.labelscans.adp4j.core;


/**
 * A listener of {@link BeforeStartEvent}/{@link AfterStopEvent} events
 * of the {@link DirectoryPoller}.
 * <p>
 * NOTE! Only implementations of this interface added
 * through this method {@link DirectoryPollerBuilder#addListener(Adp4jListener)}
 * will receive {@link BeforeStartEvent} events. 
 *
 */
public interface DirectoryPollerListener extends Adp4jListener{

	/**
	 * Invoked once before the {@link DirectoryPoller} is started.
	 * <p>
	 * NOTE! Only listeners added through this method {@link DirectoryPollerBuilder#addListener(Adp4jListener)}
	 * will receive {@link BeforeStartEvent} events. 
	 * 
	 * @param event provided by the {@link DirectoryPoller}.
	 */
	void beforeStart(BeforeStartEvent event);
	
	/**
	 * Invoked once after the {@link DirectoryPoller} is stopped.
	 * 
	 * @param event provided by the {@link DirectoryPoller}.
	 */
	void afterStop(AfterStopEvent event);
}
