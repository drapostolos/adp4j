package org.adp4j.core;

/**
 * A listener of the {@link InitialContentEvent} event of the {@link DirectoryPoller}.
 *
 */
public interface InitialContentListener extends Adp4jListener{
	/**
	 * Invoked once during the first poll-cycle of the {@link DirectoryPoller}.
	 * 
	 * @param event provided by the {@link DirectoryPoller}.
	 */
	void initialContent(InitialContentEvent event);

}
