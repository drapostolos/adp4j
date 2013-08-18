package org.sdp;

import java.io.IOException;

import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.sdp.spi.FileObject;

public class EventsTest {
	
	@Test
	public void ioExceptionEvent() throws Exception {
		// given
		DirectoryPoller dp = Mockito.mock(DirectoryPoller.class);
		IOException e = Mockito.mock(IOException.class);
		
		// when
		IoErrorRaisedEvent event = new IoErrorRaisedEvent(dp, e);
		
		// then
		Assertions.assertThat(event.getIoException()).isEqualTo(e);
		Assertions.assertThat(event.getDirectoryPoller()).isEqualTo(dp);
		
	}

	@Test
	public void fileAddedEvent() throws Exception {
		// given
		DirectoryPoller dp = Mockito.mock(DirectoryPoller.class);
		FileObject file = Mockito.mock(FileObject.class);
		
		// when
		FileAddedEvent event = new FileAddedEvent(dp, file);
		
		// then
		Assertions.assertThat(event.getFile()).isEqualTo(file);
		Assertions.assertThat(event.getDirectoryPoller()).isEqualTo(dp);
		
	}

}
