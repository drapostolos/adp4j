package org.adp4j.core;

import java.io.IOException;

import org.adp4j.core.DirectoryPoller;
import org.adp4j.core.FileAddedEvent;
import org.adp4j.core.IoErrorRaisedEvent;
import org.adp4j.spi.FileObject;
import org.adp4j.spi.PolledDirectory;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

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
		FileObject file = Mockito.mock(FileObject.class);
		
		// when
		FileAddedEvent event = new FileAddedEvent(dp, directory, file);
		
		// then
		Assertions.assertThat(event.getFile()).isEqualTo(file);
		Assertions.assertThat(event.getDirectory()).isEqualTo(directory);
		Assertions.assertThat(event.getDirectoryPoller()).isEqualTo(dp);
		
	}

}
