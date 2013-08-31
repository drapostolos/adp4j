package org.adp4j.core;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import org.adp4j.spi.PolledDirectory;

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
 */
public class DirectoryPoller implements Stoppable{
	private static final long WITH_NO_DELAY = 0;
	private static AtomicInteger threadCount = new AtomicInteger();
	private Timer timer;
	private PollerTask pollerTask;
	private FileFilter filter;
	private long pollingIntervalInMillis;
	private String threadName;
	
	// Passed to PollerTask
	ListenerNotifier notifier;
	boolean fileAddedEventEnabledForInitialContent;
	boolean parallelDirectoryPollingEnabled;
	Set<PolledDirectory> directories;
	
	public static DirectoryPollerBuilder newBuilder(){
		return new DirectoryPollerBuilder();
	}

	DirectoryPoller (DirectoryPollerBuilder builder){
		// First copy values from builder...
		directories = new LinkedHashSet<PolledDirectory>(builder.directories);
		filter = builder.filter;
		pollingIntervalInMillis = builder.pollingIntervalInMillis;
		threadName = builder.threadName;
		fileAddedEventEnabledForInitialContent = builder.fileAddedEventEnabledForInitialContent;
		parallelDirectoryPollingEnabled = builder.parallelDirectoryPollingEnabled;
		notifier = new ListenerNotifier(builder.listeners);

		// ...then check mandatory values
		if(directories.isEmpty()){
			// TODO fix error message. Should this be mandatory?
			String message = "Directory not set. directory=null";
			throw new IllegalStateException(message);
		}

		setThreadName();
		pollerTask = new PollerTask(this);
	}
	private void setThreadName() {
		if(threadName.equals(DirectoryPollerBuilder.DEFAULT_THREAD_NAME)){
			threadName = threadName + threadCount.incrementAndGet();
		}
	}
	
	void start(){
		timer = new Timer(threadName);
		timer.schedule(pollerTask, WITH_NO_DELAY, pollingIntervalInMillis);
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
	public Set<PolledDirectory> getDirectories(){
		return pollerTask.getDirectories();
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

	public void addDirectory(PolledDirectory directory){
		if(directory == null){
			throw new NullPointerException("Argument is null.");
		}
		pollerTask.addDirectory(directory);
	}

	public void removeDirectory(PolledDirectory directory){
		if(directory == null){
			throw new NullPointerException("Argument is null.");
		}
		pollerTask.removeDirectory(directory);
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
		.append(getDirectories())
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

	public boolean isParallelDirectoryPollingEnabled() {
		return parallelDirectoryPollingEnabled;
	}

	public boolean isFileAdedEventForInitialContentEnabled() {
		return fileAddedEventEnabledForInitialContent;
	}
}