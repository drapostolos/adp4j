package com.labelscans.adp4j.core;

import java.io.IOException;

import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import com.labelscans.adp4j.core.DirectoryPoller;
import com.labelscans.adp4j.core.FileAddedEvent;
import com.labelscans.adp4j.core.IoErrorRaisedEvent;
import com.labelscans.adp4j.spi.FileElement;
import com.labelscans.adp4j.spi.PolledDirectory;

public class EventsTest {
	
	@Test
	public void ioExceptionEvent() throws Exception {
		// given
		DirectoryPoller dp = Mockito.mock(DirectoryPoller.class);
		PolledDirectory directory = Mockito.mock(PolledDirectory.class);
		IOException e = Mockito.mock(IOException.class);
		
		// when
		IoErrorRaisedEvent event = new IoErrorRaisedEvent(dp, directory, e);
		
		// then
		Assertions.assertThat(event.getIoException()).isEqualTo(e);
		Assertions.assertThat(event.getDirectory()).isEqualTo(directory);
		Assertions.assertThat(event.getDirectoryPoller()).isEqualTo(dp);
		
	}

	@Test
	public void fileAddedEvent() throws Exception {
		// given
		DirectoryPoller dp = Mockito.mock(DirectoryPoller.class);
		PolledDirectory directory = Mockito.mock(PolledDirectory.class);
		FileElement file = Mockito.mock(FileElement.class);
		
		// when
		FileAddedEvent event = new FileAddedEvent(dp, directory, file);
		
		// then
		Assertions.assertThat(event.getFileElement()).isEqualTo(file);
		Assertions.assertThat(event.getDirectory()).isEqualTo(directory);
		Assertions.assertThat(event.getDirectoryPoller()).isEqualTo(dp);
		
	}

}
