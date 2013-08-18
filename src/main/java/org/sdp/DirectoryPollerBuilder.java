package org.sdp;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.sdp.spi.PolledDirectory;

/**
 * A builder class that configures and then returns a started 
 * {@link DirectoryPoller} instance.
 */
public class DirectoryPollerBuilder {
	private static final String NULL_ARGUMENT_ERROR_MESSAGE = "null argument not allowed!";
	static final String DEFAULT_THREAD_NAME = "DirectoryPoller-";
	private DirectoryPoller dp;
	PolledDirectory directory;
	
	// Optional settings
	long pollingIntervalInMillis = 1000;
	FileFilter filter = new DefaultFileFilter();
	String threadName = DEFAULT_THREAD_NAME;
	boolean fileAddedEventEnabledForInitialContent = false;
	Set<Listener> listeners = new HashSet<Listener>();
	

	/**
	 * Default constructor. Returns a new instance to 
	 * configure a {@link DirectoryPoller}.
	 */
	DirectoryPollerBuilder(){ // package-private access only.
	}

	public DirectoryPollerBuilder enableFileAddedEventsForInitialContent(){
		fileAddedEventEnabledForInitialContent = true;
		return this;
	}
	
	/**
	 * This sets the directory to monitor. Mandatory parameter.  
	 * <p>
	 * @param {@link MonitoredFileParameterized}, the directory to monitor
	 * 
	 * @return DirectoryMonitorBuilder
	 * 
	 * @throws 	NullPointerException - if the given argument is null.
	 * 			IllegalStateException - if the given {@link MonitoredFileParameterized} is not a directory.
	 */
	public DirectoryPollerBuilder setDirectory(PolledDirectory dir) {
		if(dir == null){
			throw new NullPointerException(NULL_ARGUMENT_ERROR_MESSAGE);
		}
		directory = dir;
		return this;
	}

	/**
	 * This sets the interval between each poll cycle. Optional parameter. 
	 * Default value is 1000 milliseconds.
	 * 
	 * @param interval - a positive value in milliseconds.
	 * 
	 * @return DirectoryMonitorBuilder
	 * 
	 * @throws IllegalArgumentException for negative intervals.
	 */
	public DirectoryPollerBuilder setPollingInterval(long interval, TimeUnit unit) {
		if(interval < 0){
			throw new IllegalArgumentException("Argument 'interval' is negative: " + interval);
		}
		pollingIntervalInMillis = unit.toMillis(interval)
				;
		return this;
	}

	/**
	 * Set a {@link FileFilter} to only consider {@link MonitoredFileParameterized} instances 
	 * as matched by the given filter.  Optional parameter. Optional parameter. 
	 * By default all {@link MonitoredFileParameterized} are considered.
	 * 
	 * @param filter FileFilter
	 * 
	 * @return DirectoryMonitorBuilder
	 * 
	 * @throws NullPointerException if the given argument is null.
	 */
	public DirectoryPollerBuilder setFileFilter(FileFilter filter) {
		if(filter == null){
			String message = "Argument 'filter' is: " + filter;
			throw new NullPointerException(message);
		}
		this.filter = filter;
		return this;
	}

	/**
	 * Changes the name of the associated polling thread to be equal 
	 * to the argument name. Optional parameter. 
	 * 
	 * By default each thread is named "DM-{X}", where {X} is a sequence 
	 * number. I.e: "DM-1", "DM-2" etc. etc.
	 * 
	 * @param name of thread
	 * 
	 * @return DirectoryMonitorBuilder
	 * 
	 * @throws NullPointerException if the given argument is null.
	 */
	public DirectoryPollerBuilder setThreadName(String name) {
		if(name == null){
			String message = "Null argument";
			throw new NullPointerException(message);
		}
		threadName = name;
		return this;
	}

	/**
	 * Adds a DirectoryPreListener. A DirectoryPreListener always considers the 
	 * monitored directory to be empty from the start. All added DirectoryPreListeners
	 * will be notified with a "fileAdded" event for each file contained in the 
	 * monitored directory when the {@link #start()} method is called.
	 * <p>
	 * Optional method.  
	 * <p>
	 * @param listener DirectoryPreListener
	 * 
	 * @return DirectoryMonitorBuilder
	 * 
	 * @throws NullPointerException if the given argument is null.
	 */
	public DirectoryPollerBuilder addListener(Listener listener) {
		if(listener == null){
			throw new NullPointerException("Argument is null.");
		}
		listeners.add(listener);
		return this;
	}

	/**
	 * Builds a {@link DirectoryPoller} instance and starts monitoring 
	 * the specified directory. This method will block until the first
	 * polling has completed, to allow any added {@link DirectoryPreListener}
	 * to be notified first. 
	 * 
	 * @return {@link DirectoryPoller}, monitoring the specified directory. 
	 * 
	 * @throws InterruptedException if interrupted before the first polling
	 * has finished.
	 */
	public DirectoryPoller start() {
		dp = new DirectoryPoller(this);
		dp.notifier.notifyListeners(new BeforeStartEvent(dp));
		dp.start();
		return dp;
	}
}