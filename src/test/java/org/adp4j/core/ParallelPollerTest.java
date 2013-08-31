package org.adp4j.core;

import java.util.HashSet;
import java.util.concurrent.Executors;

import org.adp4j.core.DirectoryPoller;
import org.adp4j.core.PollerTask;
import org.adp4j.spi.PolledDirectory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PollerTask.class)
public class ParallelPollerTest extends EventVerifier{

	@Test
	public void singleThreadExecutor() throws Exception {
		// given
		PowerMockito.mockStatic(Executors.class);
		DirectoryPoller dp = Mockito.mock(DirectoryPoller.class);
		dp.directories = new HashSet<PolledDirectory>();
		dp.parallelDirectoryPollingEnabled = false;

		// when
		pollerTask = new PollerTask(dp);
		
		// then
		PowerMockito.verifyStatic();
		Executors.newSingleThreadExecutor();
	}

	@Test
	public void cachedThreadPool() throws Exception {
		// given
		PowerMockito.mockStatic(Executors.class);
		DirectoryPoller dp = Mockito.mock(DirectoryPoller.class);
		dp.directories = new HashSet<PolledDirectory>();
		dp.parallelDirectoryPollingEnabled = true;

		// when
		pollerTask = new PollerTask(dp);
		
		// then
		PowerMockito.verifyStatic();
		Executors.newCachedThreadPool();
	}
}
