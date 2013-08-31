package org.adp4j.core;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.adp4j.spi.PolledDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * This is the timer thread which is executed every n milliseconds
 * according to the setting of the directory poller. It investigates the
 * directory in question and notify listeners if files are added/removed/modified,
 * or if IO Error has been raised/ceased.
 */


final class PollerTask extends TimerTask {
	private static Logger logger = LoggerFactory.getLogger(PollerTask.class);
	private final DirectoryPoller dp;
	private final Map<PolledDirectory, Poller> pollers = new LinkedHashMap<PolledDirectory, Poller>();
	private final ListenerNotifier notifier;
	private Queue<Listener> listenersToRemove = new ConcurrentLinkedQueue<Listener>();
	private Queue<Listener> listenersToAdd = new ConcurrentLinkedQueue<Listener>();
	private Queue<PolledDirectory> directoriesToAdd = new ConcurrentLinkedQueue<PolledDirectory>();
	private Queue<PolledDirectory> directoriesToRemove = new ConcurrentLinkedQueue<PolledDirectory>();;
	final ExecutorService executor;

	PollerTask(DirectoryPoller directoryPoller) {
		dp = directoryPoller;
		this.notifier = dp.notifier;
		for(PolledDirectory directory : dp.directories){
			pollers.put(directory, new Poller(directoryPoller, directory));
		}
		if(dp.parallelDirectoryPollingEnabled){
			executor = Executors.newCachedThreadPool();
		} else {
			executor = Executors.newSingleThreadExecutor();
		}
	}

	/**
	 * This method is periodically called by the {@link java.util.Timer} instance.
	 */
	public synchronized void run(){
		addRemoveListeners();
		addRemoveDirectories();
		notifier.notifyListeners(new BeforePollingCycleEvent(dp));
		try {
			executor.invokeAll(pollers.values());
		} catch (InterruptedException e) {
			String message = new StringBuilder()
			.append("Internal poller thread of the DirectoryPoller was interrupted. ")
			.append("Interruption is ignored! To stop the DirectoryPoller call its stop() ")
			.append("method: DirectoryPoller.stop().")
			.toString();
			logger.error(message);
		}
		notifier.notifyListeners(new AfterPollingCycleEvent(dp));
		addRemoveListeners();
		addRemoveDirectories();
	}

	private void addRemoveDirectories() {
		PolledDirectory directory;
		while((directory = directoriesToAdd.poll()) != null){
			pollers.put(directory, new Poller(dp, directory));
		}
		while((directory = directoriesToRemove.poll()) != null){
			pollers.remove(directory);
		}
	}

	private void addRemoveListeners() {
		Listener listener;
		while((listener = listenersToAdd.poll()) != null){
			notifier.addListener(listener);
		}
		while((listener = listenersToRemove.poll()) != null){
			notifier.removeListener(listener);
		}
	}

	void addListener(Listener listener) {
		listenersToAdd.add(listener);
	}

	void removeListener(Listener listener) {
		listenersToRemove.add(listener);
	}

	void addDirectory(PolledDirectory directory) {
		directoriesToAdd.add(directory);
	}

	void removeDirectory(PolledDirectory listener) {
		directoriesToRemove.add(listener);
	}


	synchronized void waitForExecutionToStop() {
		// This method is called after the Timer has been canceled.
		// If there is an ongoing poll while timer is canceled, this 
		// method will block until the last poll has finished executing
		// (since both this and the run() methods are synchronized).
	}

	synchronized Set<PolledDirectory> getDirectories() {
		return new LinkedHashSet<PolledDirectory>(pollers.keySet());
	}

}