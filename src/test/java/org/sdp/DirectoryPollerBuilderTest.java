package org.sdp;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class DirectoryPollerBuilderTest {
	DirectoryPollerBuilder builder = DirectoryPoller.newBuilder();	
	
	@Test(expected = NullPointerException.class)
	public void nullDirectory() throws Exception {
		builder.setDirectory(null);
	}

	@Test(expected = NullPointerException.class)
	public void nullFileFilter() throws Exception {
		builder.setFileFilter(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void negativePollingInterval() throws Exception {
		builder.setPollingInterval(-1, TimeUnit.SECONDS);
	}

	@Test(expected = NullPointerException.class)
	public void nullThreadName() throws Exception {
		builder.setThreadName(null);
	}

	@Test(expected = NullPointerException.class)
	public void nullListener() throws Exception {
		builder.addListener(null);
	}

}
