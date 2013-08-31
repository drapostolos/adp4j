package org.adp4j.core;

import java.io.IOException;

import org.adp4j.spi.PolledDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractListener implements DirectoryListener, IoErrorListener, DirectoryPollerListener, PollCycleListener{
	private static final Logger logger = LoggerFactory.getLogger(AbstractListener.class);

	@Override
	public void beforeStart(BeforeStartEvent event) {
	}

	@Override
	public void afterStop(AfterStopEvent event) {
	}

	@Override
	public void beforePollingCycle(BeforePollingCycleEvent event) {
	}

	@Override
	public void afterPollingCycle(AfterPollingCycleEvent event) {
	}

	@Override
	public void ioErrorRaised(IoErrorRaisedEvent event) {
		String message = "+IOError raised when polling directory '%s'!"; 
		PolledDirectory dir = event.getDirectory();
		IOException e = event.getIoException();
		logger.error(String.format(message, dir), e);
	}

	@Override
	public void ioErrorCeased(IoErrorCeasedEvent event) {
		String message = "-IOError ceased when polling directory '%s'!"; 
		PolledDirectory dir = event.getDirectory();
		logger.info(String.format(message, dir));
	}

	@Override
	public void initialContent(InitialContentEvent event) {
	}

	@Override
	public void fileAdded(FileAddedEvent event) {
	}

	@Override
	public void fileRemoved(FileRemovedEvent event) {
	}

	@Override
	public void fileModified(FileModifiedEvent event) {
	}


}
