package org.sdp;

import java.io.File;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import org.sdp.spi.PolledDirectory;

/**
 * A framework to monitor a directory (virtually anywhere), by using a 
 * polling mechanism.
 * <p>
 * First implement the following two interfaces:
 * <ul>
 *    <li>{@link MonitoredFileParameterized} which handles how to list files, 
 *    their names and lastModified date, in the directory you are monitoring</li>
 *    <li>{@link DirectoryListener}, which specifies the events to listen to</li>
 * </ul>
 * <p>
 * Example how to use framework, in its simplest form:
 * <blockquote><pre>
 * DirectoryMonitor&ltMyMonitoredFileImpl&gt dm = new DirectoryMonitorBuilder&ltMyMonitoredFileImpl&gt()
 * .setDirectory(new MyMonitoredFileImpl())
 * .start();
 * 
 * dm.addListener(new MyListenerImpl());
 * </pre></blockquote>
 *  
 * @param &ltT extends {@link MonitoredFileParameterized}&gt
 * 
 * @author ETXALPO
 *
 */
public class DirectoryPoller implements Stoppable{
	private static final long NO_DELAY = 0;
	private static AtomicInteger threadCount = new AtomicInteger();
	private Timer timer;
	private PollerTask pollerTask;
	private PolledDirectory directory;
	private FileFilter filter;
	private long pollingIntervalInMillis;
	private String threadName;
	ListenerNotifier notifier;
	boolean fileAddedEventEnabledForInitialContent;
	
	public static DirectoryPollerBuilder newBuilder(){
		return new DirectoryPollerBuilder();
	}

	DirectoryPoller (DirectoryPollerBuilder builder){
		// First copy values from builder...
		directory = builder.directory;
		filter = builder.filter;
		pollingIntervalInMillis = builder.pollingIntervalInMillis;
		threadName = builder.threadName;
		fileAddedEventEnabledForInitialContent = builder.fileAddedEventEnabledForInitialContent;
		notifier = new ListenerNotifier(builder.listeners);

		// ...then check mandatory values
		if(directory == null){
			// TODO fix error message.
			String message = "Directory not set. directory=null";
			throw new IllegalStateException(message);
		}

		setThreadName();
	}
	private void setThreadName() {
		if(threadName.equals(DirectoryPollerBuilder.DEFAULT_THREAD_NAME)){
			threadName = threadName + threadCount.incrementAndGet();
		}
	}
	
	void start(){
		pollerTask = new PollerTask(this);
		timer = new Timer(threadName);
		timer.schedule(pollerTask, NO_DELAY, pollingIntervalInMillis);
	}

	/**
	 * Stops monitoring the directory for events. This method will block
	 * until:
	 * <ul>
	 * <li>all any ongoing notifications has completed</li>
	 * <li>the call to {@link DirectoryListener#afterStop(File)} has completed.</li>
	 * </ul> 
	 */
	public void stop(){
		timer.cancel();
		pollerTask.waitForExecutionToStop();
		notifier.notifyListeners(new AfterStopEvent(this));
	}
	
	/**
	 * Returns the monitored directory.
	 * 
	 * @return {@link MonitoredFileParameterized} - the monitored directory
	 */
	public PolledDirectory getDirectory(){
		return directory;
	}

	/**
	 * Returns the polling interval in milliseconds.
	 * 
	 * @return long - the polling interval in milliseconds
	 */
	public long getPollingIntervalInMillis(){
		return pollingIntervalInMillis;
	}
	
	/**
	 * Returns the used {@link FileFilter}.
	 * 
	 * @return {@link FileFilter} - the used FileFilter.
	 */
	public FileFilter getFileFilter(){
		return filter;
	}

	/**
	 * Registers the given <i>listener</i> in this DirectoryMonitor. 
	 * Registering an already registered listener will be ignored.
	 * 
	 * @param listener {@link DirectoryListener}
	 * 
	 * @throws NullPointerException - if the given argument is null.
	 * 
	 * TODO beforeStartevent will not be called... fix javadoc
	 */
	public void addListener(Listener listener){
		if(listener == null){
			throw new NullPointerException("Argument is null.");
		}
		pollerTask.addListener(listener);
	}

	/**
	 * Removes the given <i>listener</i> from this DirectoryMonitor.
	 * Removing a listener which has not previously been added will be ignored.
	 * 
	 * @param listener {@link DirectoryListener}
	 * 
	 * @throws NullPointerException - if the given argument is null.
	 */
	public void removeListener(Listener listener){
		if(listener == null){
			throw new NullPointerException("Argument is null.");
		}
		pollerTask.removeListener(listener);
	}

	/**
	 * Returns a string representation of this Directory monitor, 
	 * in the format: "{thread-name}: {directory-path} [polling every {polling-interval} milliseconds]" 
	 * I.e: "DM-1: c:\my\directory [polling every: 1000 milliseconds]" 
	 * 
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder()
		.append(getThreadName())
		.append(": ")
		.append(getDirectory())
		.append(" [polling every: ")
		.append(getPollingIntervalInMillis())
		.append(" milliseconds]");
		return sb.toString();
	}

	/**
	 * Returns the name of the associated polling thread.
	 * 
	 * @return String - name of associated polling thread.
	 */
	public String getThreadName() {
		return threadName;
	}
}