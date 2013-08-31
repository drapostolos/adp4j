package org.adp4j.core;

import java.util.concurrent.TimeUnit;

import org.adp4j.core.DirectoryPoller;
import org.adp4j.core.DirectoryPollerBuilder;
import org.junit.Test;

public class DirectoryPollerBuilderTest {
	DirectoryPollerBuilder builder = DirectoryPoller.newBuilder();	
	
	@Test(expected = NullPointerException.class)
	public void nullDirectory() throws Exception {
		builder.addDirectory(null);
	}

	@Test(expected = NullPointerException.class)
	public void nullFileFilter() throws Exception {
		builder.setDefaultFileFilter(null);
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
