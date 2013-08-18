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
public class ListenerThrowsExceptionTest {

	@Test
	@PrepareForTest(LoggerFactory.class)
	public void listenerThrowsException() throws Exception {

		// given
		Logger loggerMock = Mockito.mock(Logger.class);
		PowerMockito.mockStatic(LoggerFactory.class);
		Mockito.when(LoggerFactory.getLogger(ListenerNotifier.class)).thenReturn(loggerMock);
		AbstractListener listenerMock = Mockito.mock(AbstractListener.class);
		Mockito.doThrow(RuntimeException.class).when(listenerMock).beforeStart(Mockito.any(BeforeStartEvent.class));
		PolledDirectory directoryMock = Mockito.mock(PolledDirectory.class);

		// when
		DirectoryPoller.newBuilder()
		.addListener(listenerMock)
		.setDirectory(directoryMock)
		.start();

		// then
		Mockito.verify(loggerMock).error(Mockito.anyString(), Mockito.any(Throwable.class));
	}
}