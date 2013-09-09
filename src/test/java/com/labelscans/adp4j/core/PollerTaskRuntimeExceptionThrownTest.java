package com.labelscans.adp4j.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labelscans.adp4j.core.AbstractAdp4jListener;
import com.labelscans.adp4j.core.Adp4jListener;
import com.labelscans.adp4j.core.AfterPollingCycleEvent;
import com.labelscans.adp4j.core.BeforePollingCycleEvent;
import com.labelscans.adp4j.core.DefaultFileFilter;
import com.labelscans.adp4j.core.DirectoryPoller;
import com.labelscans.adp4j.core.InitialContentEvent;
import com.labelscans.adp4j.core.ListenerNotifier;
import com.labelscans.adp4j.core.PollerTask;
import com.labelscans.adp4j.spi.PolledDirectory;

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
		listenerMock = Mockito.mock(AbstractAdp4jListener.class);
		inOrder = Mockito.inOrder(listenerMock);
		DirectoryPoller dp = Mockito.mock(DirectoryPoller.class);
		Mockito.when(dp.getDefaultFileFilter()).thenReturn(new DefaultFileFilter());
		dp.directories = new HashSet<PolledDirectory>(Arrays.asList(directory));
		dp.notifier = new ListenerNotifier(new HashSet<Adp4jListener>(Arrays.asList(listenerMock)));
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
