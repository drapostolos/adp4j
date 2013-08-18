package org.sdp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sdp.spi.PolledDirectory;
import org.sdp.spi.FileObject;

public class AcceptanceTest extends EventVerifier {

	@Before
	public void testName() throws Exception {
		directoryMock = Mockito.mock(PolledDirectory.class);
		listenerMock = Mockito.mock(AbstractListener.class);
		inOrder = Mockito.inOrder(listenerMock);
	}

	@Test
	public void fileAddedEventEnabledForInitialContent() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list("file1.txt/1"));
		
		// when
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.setDirectory(directoryMock)
				.addListener(listenerMock)
				.enableFileAddedEventsForInitialContent()
				.setPollingInterval(10, TimeUnit.MILLISECONDS)
				.start();
		TimeUnit.MILLISECONDS.sleep(50);
		dp.stop();
		
		// then
		verifyEventsInOrder(
				BeforeStartEvent.class,
				BeforePollingCycleEvent.class,
				FileAddedEvent.class,
				InitialContentEvent.class,
				AfterPollingCycleEvent.class,
				BeforePollingCycleEvent.class,
				AfterPollingCycleEvent.class,
				AfterStopEvent.class
				);
	}
	
	@Test
	public void addListenerAfterStart() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list())
		.thenReturn(list("file.txt/12"));
		
		// when
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.setDirectory(directoryMock)
				.setPollingInterval(10, TimeUnit.MILLISECONDS)
				.start();
		dp.addListener(listenerMock);
		TimeUnit.MILLISECONDS.sleep(50);
		dp.stop();
		
		// then
		verifyEventsInOrder(
				BeforePollingCycleEvent.class,
				FileAddedEvent.class,
				AfterPollingCycleEvent.class,
				AfterStopEvent.class
				);
	}
	
	@Test
	public void removeListener() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list())
		.thenReturn(list("file.txt/12"));
		
		// when
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.setDirectory(directoryMock)
				.addListener(listenerMock)
				.setPollingInterval(10, TimeUnit.MILLISECONDS)
				.start();
		TimeUnit.MILLISECONDS.sleep(10);
		dp.removeListener(listenerMock);
		TimeUnit.MILLISECONDS.sleep(20);
		dp.stop();
		
		Mockito.verify(listenerMock, Mockito.times(0)).afterStop(Mockito.any(AfterStopEvent.class));
	}
	
	@Test
	public void toStringValue() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list());
		
		// when
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.setDirectory(directoryMock)
				.start();
		dp.stop();
		
		Assertions.assertThat(dp.toString()).matches("DM-\\d+: .*\\[polling every: 1000 milliseconds\\]");
		
	}
	
	@Test
	public void OneSuccesfulPoll() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list());
		
		// when
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.addListener(listenerMock)
				.setDirectory(directoryMock)
				.setPollingInterval(200, TimeUnit.MILLISECONDS)
				.start();
		TimeUnit.MILLISECONDS.sleep(10);
		dp.stop();
		
		// then
		Assertions.assertThat(dp.getThreadName()).matches("DM-\\d+");
		verifyEventsInOrder(
				BeforeStartEvent.class,
				BeforePollingCycleEvent.class,
				InitialContentEvent.class,
				AfterPollingCycleEvent.class,
				AfterStopEvent.class
				);
		Mockito.verifyNoMoreInteractions(listenerMock);
	}
	
	@Test
	public void OneSuccesfulPollUsingFileFilter() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list("a.txt/12", "b.xml/11"));
		
		final List<FileObject> files = new ArrayList<FileObject>();
		Mockito.doAnswer(new Answer<InitialContentEvent>() {
			@Override
			public InitialContentEvent answer(InvocationOnMock invocation) throws Throwable {
				Set<FileObject> s = ((InitialContentEvent) invocation.getArguments()[0]).getFiles();
				files.addAll(s);
				return null;
			}
		}).when(listenerMock).initialContent(Mockito.any(InitialContentEvent.class));

		// when
		DirectoryPoller dp = DirectoryPoller.newBuilder()
				.addListener(listenerMock)
				.setDirectory(directoryMock)
				.setFileFilter(new RegexFileFilter(".*\\.txt"))
				.setThreadName("NAME")
				.setPollingInterval(200, TimeUnit.MILLISECONDS)
				.start();
		TimeUnit.MILLISECONDS.sleep(10);
		dp.stop();
		
		// then
		Assertions.assertThat(files).isEqualTo(list("a.txt/12"));
		Assertions.assertThat(dp.getThreadName()).isEqualTo("NAME");
		Assertions.assertThat(dp.getPollingIntervalInMillis()).isEqualTo(200);
		verifyEventsInOrder(
				BeforeStartEvent.class,
				BeforePollingCycleEvent.class,
				InitialContentEvent.class,
				AfterPollingCycleEvent.class,
				AfterStopEvent.class
				);
		Mockito.verifyNoMoreInteractions(listenerMock);
	}
	
//	@Test
//	@Ignore
//	public void initiallyIoErrorRaisedThenCeased() throws Exception {
//		// given 
//		Mockito.when(directoryMock.listFiles())
//		.thenThrow(new IOException())
//		.thenReturn(null)
//		.thenReturn(list("fileA/1", "fileB/1"));
//		
//		// when
//		DirectoryPoller dp = DirectoryPoller.newBuilder()
//				.addListener(listenerMock)
//				.setDirectory(directoryMock)
//				.setPollingInterval(10, TimeUnit.MILLISECONDS)
//				.start();
//		TimeUnit.MILLISECONDS.sleep(25);
//		dp.stop();
//		
//		// then
//		verifyInOrder_BeforeStartEvent();
//
//		// cycle#1
//		verifyInOrder_BeforePollingCycleEvent();
//		verifyInOrder_ioErrorRaisedEvent();
//		verifyInOrder_AfterPollingCycleEvent();
//		
//		// cycle#2
//		verifyInOrder_BeforePollingCycleEvent();
//		verifyInOrder_AfterPollingCycleEvent();
//		
//		// cycle#3
//		verifyInOrder_BeforePollingCycleEvent();
//		verifyInOrder_ioErrorCeasedEvent();
//		verifyInOrder_InitialDirectoryContentEvent();
//		verifyInOrder_AfterPollingCycleEvent();
//
//		verifyInOrder_AfterStopEvent();
//		
//		Mockito.verifyNoMoreInteractions(listenerMock);
//	}
//	
//	@Test
//	@Ignore
//	public void removeOnefileAndModifyOneFile() throws Exception {
//		// given 
//		Mockito.when(directoryMock.listFiles())
//		.thenReturn(list("fileA/1"))
//		.thenReturn(list("fileA/1", "fileB/1"))
//		.thenReturn(list("fileA/2", "fileC/1"));
//		
//		// when
//		DirectoryPoller dp = DirectoryPoller.newBuilder()
//				.addListener(listenerMock)
//				.setDirectory(directoryMock)
//				.setPollingInterval(100, TimeUnit.MILLISECONDS)
//				.start();
//		TimeUnit.MILLISECONDS.sleep(350);
//		dp.stop();
//		
//		// then
//		verifyInOrder_BeforeStartEvent();
//
//		// cycle#1
//		verifyInOrder_BeforePollingCycleEvent();
//		verifyInOrder_InitialDirectoryContentEvent();
//		verifyInOrder_AfterPollingCycleEvent();
//		
//		// cycle#2
//		verifyInOrder_BeforePollingCycleEvent();
//		verifyInOrder_fileAddedEvent();
//		verifyInOrder_AfterPollingCycleEvent();
//
//		// cycle#3
//		verifyInOrder_BeforePollingCycleEvent();
//		verifyInOrder_FileRemovedEvent();
//		verifyInOrder_FileAddedEvent();
//		verifyInOrder_FileModifiedEvent();
//		verifyInOrder_AfterPollingCycleEvent();
//
//		// cycle#4
//		verifyInOrder_BeforePollingCycleEvent();
//		verifyInOrder_AfterPollingCycleEvent();
//
//		verifyInOrder_AfterStopEvent();
//		
//		Mockito.verifyNoMoreInteractions(listenerMock);
//	}
//	
//	@Test
//	@Ignore
//	public void initialDirectoryContent() throws Exception {
//		// given 
//		Mockito.when(directoryMock.listFiles(Mockito.any(FileFilter.class)))
//		.thenReturn(list("fileA/1", "fileB/1"))
//		.thenReturn(list("fileA/1", "fileB/1"));
//		
//		final List<PolledFile> files = new ArrayList<PolledFile>();
//		Mockito.doAnswer(new Answer<InitialContentEvent>() {
//			@Override
//			public InitialContentEvent answer(InvocationOnMock invocation) throws Throwable {
//				Set<PolledFile> s = ((InitialContentEvent) invocation.getArguments()[0]).getFiles();
//				files.addAll(s);
//				return null;
//			}
//		}).when(listenerMock).initialContent(Mockito.any(InitialContentEvent.class));
//
//		// when
//		DirectoryPoller dp = DirectoryPoller.newBuilder()
//				.addListener(listenerMock)
//				.setDirectory(directoryMock)
//				.setPollingInterval(10, TimeUnit.MILLISECONDS)
//				.start();
//		TimeUnit.MILLISECONDS.sleep(15);
//		dp.stop();
//		
//		// then
//		Assertions.assertThat(files).containsAll(list("fileA/1", "fileB/1"));
//		
//		verifyInOrder_BeforeStartEvent();
//
//		// cycle#1
//		verifyInOrder_BeforePollingCycleEvent();
//		verifyInOrder_InitialDirectoryContentEvent();
//		verifyInOrder_AfterPollingCycleEvent();
//		
//		// cycle#2
//		verifyInOrder_BeforePollingCycleEvent();
//		verifyInOrder_AfterPollingCycleEvent();
//
//		verifyInOrder_AfterStopEvent();
//		
//		Mockito.verifyNoMoreInteractions(listenerMock);
//	}
	
}
