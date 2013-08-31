package org.adp4j.core;

import org.adp4j.core.DirectoryPoller;
import org.adp4j.spi.PolledDirectory;
import org.junit.Test;
import org.mockito.Mockito;

public class DirectoryPollerTest {

	@Test(expected = IllegalStateException.class)
	public void directoryNeverSet() {
		DirectoryPoller.newBuilder().start();
	}

	@Test(expected = NullPointerException.class)
	public void addNullDirectory() {
		// given
		PolledDirectory directoryMock = Mockito.mock(PolledDirectory.class);
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.addDirectory(directoryMock)
				.start();
		
		// when
		dp.addDirectory(null);
	}

	@Test(expected = NullPointerException.class)
	public void removeNullDirectory() {
		// given
		PolledDirectory directoryMock = Mockito.mock(PolledDirectory.class);
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.addDirectory(directoryMock)
				.start();
		
		// when
		dp.removeDirectory(null);
	}

	@Test(expected = NullPointerException.class)
	public void addNullListener() {
		// given
		PolledDirectory directoryMock = Mockito.mock(PolledDirectory.class);
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.addDirectory(directoryMock)
				.start();
		
		// when
		dp.addListener(null);
	}

	@Test(expected = NullPointerException.class)
	public void removeNullListener() {
		// given
		PolledDirectory directoryMock = Mockito.mock(PolledDirectory.class);
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.addDirectory(directoryMock)
				.start();
		
		// when
		dp.removeListener(null);
	}

}
