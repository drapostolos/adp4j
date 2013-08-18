package org.sdp;

import org.junit.Test;
import org.mockito.Mockito;
import org.sdp.spi.PolledDirectory;

public class DirectoryPollerTest {

	@Test(expected = IllegalStateException.class)
	public void directoryNeverSet() {
		DirectoryPoller.newBuilder().start();
	}

	@Test(expected = NullPointerException.class)
	public void addNullListener() {
		// given
		PolledDirectory directoryMock = Mockito.mock(PolledDirectory.class);
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.setDirectory(directoryMock)
				.start();
		
		// when
		dp.addListener(null);
	}

	@Test(expected = NullPointerException.class)
	public void removeNullListener() {
		// given
		PolledDirectory directoryMock = Mockito.mock(PolledDirectory.class);
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.setDirectory(directoryMock)
				.start();
		
		// when
		dp.removeListener(null);
	}

}
