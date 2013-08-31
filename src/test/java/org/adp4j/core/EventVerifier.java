package org.adp4j.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.adp4j.core.AbstractListener;
import org.adp4j.core.AfterPollingCycleEvent;
import org.adp4j.core.AfterStopEvent;
import org.adp4j.core.BeforePollingCycleEvent;
import org.adp4j.core.BeforeStartEvent;
import org.adp4j.core.DirectoryPoller;
import org.adp4j.core.FileAddedEvent;
import org.adp4j.core.FileModifiedEvent;
import org.adp4j.core.FileRemovedEvent;
import org.adp4j.core.InitialContentEvent;
import org.adp4j.core.IoErrorCeasedEvent;
import org.adp4j.core.IoErrorRaisedEvent;
import org.adp4j.core.PollerTask;
import org.adp4j.spi.FileObject;
import org.adp4j.spi.PolledDirectory;
import org.mockito.InOrder;
import org.mockito.Mockito;

class EventVerifier {

	protected PollerTask pollerTask;
	protected AbstractListener listenerMock;
	protected InOrder inOrder;
	protected Set<PolledDirectory> directories = new LinkedHashSet<PolledDirectory>();
	protected PolledDirectory directoryMock;
	protected DirectoryPoller directoryPollerMock;

	protected void executeNumberOfPollCycles(int numOfPollCycles) {
		for(int i = 0; i < numOfPollCycles; i++){
			pollerTask.run();
		}
	}

	protected void verifyEventsInOrder(Class<?>... events) throws Exception {
		for(Class<?> event : events){
			Method m = getClass().getMethod("verifyInOrder_" + event.getSimpleName());
			m.invoke(this);
		}
	}

	public void verifyInOrder_InitialContentEvent() {
		inOrder.verify(listenerMock).initialContent(Mockito.any(InitialContentEvent.class));
	}
	public void verifyInOrder_BeforePollingCycleEvent() {
		inOrder.verify(listenerMock).beforePollingCycle(Mockito.any(BeforePollingCycleEvent.class));
	}
	public void verifyInOrder_AfterPollingCycleEvent() {
		inOrder.verify(listenerMock).afterPollingCycle(Mockito.any(AfterPollingCycleEvent.class));
	}

//	public void verifyInOrder_FileAddedEvent() {
//		verifyInOrder_FileAddedEvent(1);
//	}
	public void verifyInOrder_FileAddedEvent() {
		inOrder.verify(listenerMock).fileAdded(Mockito.any(FileAddedEvent.class));
	}
	public void verifyInOrder_FileRemovedEvent() {
		inOrder.verify(listenerMock).fileRemoved(Mockito.any(FileRemovedEvent.class));
	}
	public void verifyInOrder_FileModifiedEvent() {
		inOrder.verify(listenerMock).fileModified(Mockito.any(FileModifiedEvent.class));
	}

	public void verifyInOrder_IoErrorCeasedEvent() {
		inOrder.verify(listenerMock).ioErrorCeased(Mockito.any(IoErrorCeasedEvent.class));
	}
	public void verifyInOrder_IoErrorRaisedEvent() {
		inOrder.verify(listenerMock).ioErrorRaised(Mockito.any(IoErrorRaisedEvent.class));
	}

	public void verifyInOrder_BeforeStartEvent() {
		inOrder.verify(listenerMock).beforeStart(Mockito.any(BeforeStartEvent.class));
	}
	public void verifyInOrder_AfterStopEvent() {
		inOrder.verify(listenerMock).afterStop(Mockito.any(AfterStopEvent.class));
	}

	/*
	 * input argument is in the form: "name/lastModified"
	 * Example "my.txt/1233"
	 */
	public List<FileObject> list(String... files) throws Exception {
		List<FileObject> list = new ArrayList<FileObject>();
		for(String nameAndTime : files){
			String[] t = nameAndTime.split("/");
			String fileName = t[0];
			long lastModified = Long.parseLong(t[1]);
			FileObject file = new StubbedFileObject(fileName, lastModified);
			list.add(file);
		}
		return list;
	}

}
