package org.sdp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sdp.spi.PolledDirectory;
import org.sdp.spi.FileObject;

public class PollerTaskTest extends EventVerifier {

	@Before
	public void testFixture() throws Exception {
		directoryMock = Mockito.mock(PolledDirectory.class);
		listenerMock = Mockito.mock(AbstractListener.class);
		inOrder = Mockito.inOrder(listenerMock);
		directoryPollerMock = Mockito.mock(DirectoryPoller.class);
		Mockito.when(directoryPollerMock.getDirectory()).thenReturn(directoryMock);
		Mockito.when(directoryPollerMock.getFileFilter()).thenReturn(new DefaultFileFilter());
		directoryPollerMock.notifier = new ListenerNotifier(new HashSet<Listener>(Arrays.asList(listenerMock)));
		pollerTask = new PollerTask(directoryPollerMock);
	}

	@Test
	public void filterOutAFile() throws Exception {
		// given 
		// This will change the filter used by the PollerTask
		Mockito.when(directoryPollerMock.getFileFilter()).thenReturn(new RegexFileFilter("file[AB]"));
		pollerTask = new PollerTask(directoryPollerMock);

		Mockito.when(directoryMock.listFiles())
		.thenReturn(list("fileA/1"))
		.thenReturn(list("fileA/1", "fileB/1"))
		.thenReturn(list("fileA/1", "fileB/1", "fileC/1"));

		// when
		executeNumberOfPollCycles(3);
		// then
		verifyEventsInOrder(
				// poll-cycle#1
				BeforePollingCycleEvent.class, 
				InitialContentEvent.class, 
				AfterPollingCycleEvent.class,

				// poll-cycle#2
				BeforePollingCycleEvent.class,
				FileAddedEvent.class,
				AfterPollingCycleEvent.class,

				// poll-cycle#3
				BeforePollingCycleEvent.class,
				AfterPollingCycleEvent.class
				);
		Mockito.verifyNoMoreInteractions(listenerMock);
	}

	@Test
	public void addRemoveListers() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list("fileA/1"))
		.thenReturn(list("fileA/1", "fileB/1"))
		.thenReturn(list("fileA/1", "fileB/1", "fileC/1"));

		// when
		pollerTask.run(); // poll-cycle 1
		pollerTask.removeListener(listenerMock);
		pollerTask.run(); // poll-cycle 2
		pollerTask.addListener(listenerMock);
		pollerTask.run(); // poll-cycle 3

		// then
		verifyEventsInOrder(
				// poll-cycle#1
				BeforePollingCycleEvent.class, 
				InitialContentEvent.class, 
				AfterPollingCycleEvent.class,

				// poll-cycle#2
				// Nothing should happen

				// poll-cycle#3
				BeforePollingCycleEvent.class,
				FileAddedEvent.class,
				AfterPollingCycleEvent.class
				);
		Mockito.verifyNoMoreInteractions(listenerMock);
	}

	@Test
	public void OneSuccesfulPoll() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list());

		// when
		executeNumberOfPollCycles(1);

		// then
		verifyEventsInOrder(
				BeforePollingCycleEvent.class, 
				InitialContentEvent.class, 
				AfterPollingCycleEvent.class
				);
		Mockito.verifyNoMoreInteractions(listenerMock);
	}

	@Test
	public void initiallyIoErrorRaisedThenCeased() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenThrow(new IOException("thrown from unit test!"))
		.thenReturn(null)
		.thenReturn(list("fileA/1", "fileB/1"));

		// when
		executeNumberOfPollCycles(3);

		// then
		verifyEventsInOrder(
				// poll-cycle#1
				BeforePollingCycleEvent.class, 
				IoErrorRaisedEvent.class, 
				AfterPollingCycleEvent.class,

				// poll-cycle#2
				BeforePollingCycleEvent.class, 
				AfterPollingCycleEvent.class,

				// poll-cycle#3
				BeforePollingCycleEvent.class,
				IoErrorCeasedEvent.class,
				InitialContentEvent.class,
				AfterPollingCycleEvent.class
				);
		Mockito.verifyNoMoreInteractions(listenerMock);
	}


	@Test
	public void IoErrorRaisedByLastModifiedThenCeased() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list("fileA/1", "fileB/1"))
		.thenReturn(list("fileA/0", "fileB/1"))
		.thenReturn(list("fileA/1", "fileB/1"));

		// when
		executeNumberOfPollCycles(3);

		// then
		verifyEventsInOrder(
				// poll-cycle#1
				BeforePollingCycleEvent.class, 
				InitialContentEvent.class, 
				AfterPollingCycleEvent.class,

				// poll-cycle#2
				BeforePollingCycleEvent.class, 
				IoErrorRaisedEvent.class,
				AfterPollingCycleEvent.class,

				// poll-cycle#3
				BeforePollingCycleEvent.class,
				IoErrorCeasedEvent.class,
				AfterPollingCycleEvent.class
				);
		Mockito.verifyNoMoreInteractions(listenerMock);
	}

	@Test
	public void directoryPollerExceptionThrown() throws Exception {
		// given
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list("fileA/1"))
		.thenThrow(new DirectoryPollerException());

		// when
		executeNumberOfPollCycles(2);

		// then
		verifyEventsInOrder(
				// poll-cycle#1
				BeforePollingCycleEvent.class, 
				InitialContentEvent.class, 
				AfterPollingCycleEvent.class,

				// poll-cycle#2
				BeforePollingCycleEvent.class, 
				AfterPollingCycleEvent.class
				);
		Mockito.verifyNoMoreInteractions(listenerMock);
	}

	@Test
	public void addRemoveModifyFiles() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list("fileA/1"))
		.thenReturn(list("fileA/1", "fileB/1"))
		.thenReturn(list("fileA/2", "fileC/1"));

		// when
		executeNumberOfPollCycles(4);

		// then
		verifyEventsInOrder(
				// poll-cycle#1
				BeforePollingCycleEvent.class, 
				InitialContentEvent.class, 
				AfterPollingCycleEvent.class,

				// poll-cycle#2
				BeforePollingCycleEvent.class, 
				FileAddedEvent.class,
				AfterPollingCycleEvent.class,

				// poll-cycle#3
				BeforePollingCycleEvent.class,
				FileRemovedEvent.class,
				FileAddedEvent.class,
				FileModifiedEvent.class,
				AfterPollingCycleEvent.class,

				// poll-cycle#4
				BeforePollingCycleEvent.class,
				AfterPollingCycleEvent.class
				);
		Mockito.verifyNoMoreInteractions(listenerMock);
	}

	@Test
	public void initialContent() throws Exception {
		// given 
		Mockito.when(directoryMock.listFiles())
		.thenReturn(list("fileA/1", "fileB/1"))
		.thenReturn(list("fileA/1", "fileB/1"));

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
		executeNumberOfPollCycles(2);

		// then
		Assertions.assertThat(files).containsAll(list("fileA/1", "fileB/1"));
		verifyEventsInOrder(
				// poll-cycle#1
				BeforePollingCycleEvent.class,
				InitialContentEvent.class,
				AfterPollingCycleEvent.class,

				// poll-cycle#2
				BeforePollingCycleEvent.class,
				AfterPollingCycleEvent.class
				);
		Mockito.verifyNoMoreInteractions(listenerMock);
	}

}
