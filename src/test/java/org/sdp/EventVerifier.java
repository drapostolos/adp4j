package org.sdp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sdp.spi.PolledDirectory;
import org.sdp.spi.FileObject;

class EventVerifier {

	protected PollerTask pollerTask;
	protected AbstractListener listenerMock;
	protected InOrder inOrder;
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
			FileObject file = new StubbedPolledFile(fileName, lastModified);
			list.add(file);
		}
		return list;
	}

}
