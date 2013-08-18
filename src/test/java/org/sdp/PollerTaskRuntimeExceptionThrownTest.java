package org.sdp;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sdp.spi.PolledDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PowerMockRunner.class)
public class PollerTaskRuntimeExceptionThrownTest extends EventVerifier{

	@Test
	@PrepareForTest(LoggerFactory.class)
	public void runtimeExceptionThrown() throws Exception {
		// given fixture
		Logger loggerMock = Mockito.mock(Logger.class);
		PowerMockito.mockStatic(LoggerFactory.class);
		Mockito.when(LoggerFactory.getLogger(PollerTask.class)).thenReturn(loggerMock);
		PolledDirectory directory = Mockito.mock(PolledDirectory.class);
		listenerMock = Mockito.mock(AbstractListener.class);
		inOrder = Mockito.inOrder(listenerMock);
		DirectoryPoller dp = Mockito.mock(DirectoryPoller.class);
		Mockito.when(dp.getDirectory()).thenReturn(directory);
		Mockito.when(dp.getFileFilter()).thenReturn(new DefaultFileFilter());
		dp.notifier = new ListenerNotifier(new HashSet<Listener>(Arrays.asList(listenerMock)));
		pollerTask = new PollerTask(dp);
		
		Mockito.when(directory.listFiles())
		.thenReturn(list("fileA/1"))
		.thenThrow(new RuntimeException());

		// when
		executeNumberOfPollCycles(2);
		
		// then
		verifyEventsInOrder(
				// poll-cycle#1
				BeforePollingCycleEvent.class,
				InitialContentEvent.class,
				AfterPollingCycleEvent.class,
		
				// poll-cycle#2
				BeforePollingCycleEvent.class,
				AfterPollingCycleEvent.class
				);
		
		Mockito.verify(loggerMock).error(Mockito.anyString(), Mockito.any(IOException.class));
		Mockito.verifyNoMoreInteractions(listenerMock);
	}
}
