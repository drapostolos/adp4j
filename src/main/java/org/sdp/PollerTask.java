package org.sdp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.sdp.spi.PolledDirectory;
import org.sdp.spi.FileObject;
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
	private final Map<FileObject, Long> currentListedFiles = new LinkedHashMap<FileObject, Long>();
	private final Map<FileObject, Long> previousListedFiles = new LinkedHashMap<FileObject, Long>();
	private final List<FileObject> modifiedFiles = new ArrayList<FileObject>();
	private final DirectoryPoller dp;
	private final PolledDirectory directory;
	private final FileFilter filter;
	private final ListenerNotifier notifier;
	private boolean isSecondPollCycleOrLater = false;
	private boolean isFilesystemUnaccessible = true;
	private HashMapDiffer<FileObject, Long> mapComparer;
	private Queue<Listener> listenersToRemove = new ConcurrentLinkedQueue<Listener>();
	private Queue<Listener> listenersToAdd = new ConcurrentLinkedQueue<Listener>();
	private boolean fileAddedEventEnabledForInitialContent;

	PollerTask(DirectoryPoller directoryPoller) {
		dp = directoryPoller;
		directory = dp.getDirectory();
		filter = dp.getFileFilter();
		notifier = dp.notifier;
		fileAddedEventEnabledForInitialContent = dp.fileAddedEventEnabledForInitialContent;
	}

	/**
	 * This method is periodically called by the {@link java.util.Timer} instance.
	 */
	public synchronized void run(){
		addRemoveListeners();
		notifyListenersOfNewEvent(new BeforePollingCycleEvent(dp));
		poll();
		notifyListenersOfNewEvent(new AfterPollingCycleEvent(dp));
		addRemoveListeners();
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

	private void poll() {
		listCurrentFilesAndNotifyListenersIfIoErrorRaisedOrCeased();
		if(isFilesystemAccessible()){
			updateModifiedFiles();
			setComparerForCurrentVersusPreviousListedFiles();
			if(isDirectoryModified()){
				if(isSecondPollCycleOrLater() || fileAddedEventEnabledForInitialContent){
					notifyListenersWithRemovedAddedModifiedFiles();
				}
				copyCurrentListedFilesToPrevious();
			}
			if(isFirstPollCycle() ){
				// Only do this once, i.e. the first time files are listed successfully. 
				notifyListenersOfNewEvent(new InitialContentEvent(dp, new HashSet<FileObject>(currentListedFiles.keySet())));
				isSecondPollCycleOrLater = true;
			}
		}
	}
		
	boolean isSecondPollCycleOrLater(){
		return isSecondPollCycleOrLater;
	}
	boolean isFirstPollCycle(){
		return !isSecondPollCycleOrLater();
	}

	private void listCurrentFilesAndNotifyListenersIfIoErrorRaisedOrCeased(){
		try {
			List<FileObject> files = directory.listFiles();
			if(files == null){
				String message = new StringBuilder()
				.append("Unknown underlying IO-error when listing files ")
				.append("in directory: '%s'. Method listFiles() returned null.")
				.toString();
				throw new IOException(String.format(message, directory));
			}
			Map<FileObject, Long> temp = new LinkedHashMap<FileObject, Long>();
			for(FileObject file : files){
				if(filter.accept(file)){
					long lastModified = file.lastModified();
					if(lastModified == 0L){
						throw new IOException("Unknown underlying IO-Error. Method 'lastModified()' returned '0L' for file '" + file + "'");
					}
					temp.put(file, lastModified);
				}
			}
			if(isFilesystemUnaccessible()){
				notifyListenersOfNewEvent(new IoErrorCeasedEvent(dp));
				isFilesystemUnaccessible = true;
			}
			currentListedFiles.clear();
			currentListedFiles.putAll(temp);
		} catch (IOException e) {
			if(isFilesystemAccessible()){
				isFilesystemUnaccessible = false;
				notifyListenersOfNewEvent(new IoErrorRaisedEvent(dp, e));
			}
		} catch(DirectoryPollerException e){
			// Silently wait fore next poll.
		} catch (RuntimeException e){
			String message = new StringBuilder()
			.append("RuntimeException was thrown by class '%s'. ")
			.append("See underlying exception for more info.")
			.toString();
			logger.error(String.format(message, directory.getClass().getName()), e);
		}
	}

	private void updateModifiedFiles() {
		modifiedFiles.clear();
		for(FileObject f : currentListedFiles.keySet()){
			if(isFileModified(f, currentListedFiles.get(f))){
				modifiedFiles.add(f);
			}
		}
	}
	private boolean isFileModified(FileObject f, long lastModified) {
		if(previousListedFiles.containsKey(f)){
			long previous = previousListedFiles.get(f);
			return lastModified != previous;
		}
		return false;
	}

	private void setComparerForCurrentVersusPreviousListedFiles(){
		mapComparer = new HashMapDiffer<FileObject, Long>(previousListedFiles, currentListedFiles);
	}

	private boolean isDirectoryModified() {
		return !modifiedFiles.isEmpty() || mapComparer.hasDiff();
	}

	private void notifyListenersWithRemovedAddedModifiedFiles(){
		for(FileObject file : mapComparer.getRemoved().keySet()){
			notifyListenersOfNewEvent(new FileRemovedEvent(dp, file));
		}
		
		for(FileObject file : mapComparer.getAdded().keySet()){
			notifyListenersOfNewEvent(new FileAddedEvent(dp, file));
		}
		for(FileObject file : modifiedFiles){
			notifyListenersOfNewEvent(new FileModifiedEvent(dp, file));
		}
	}

	private void copyCurrentListedFilesToPrevious(){
		previousListedFiles.clear();
		previousListedFiles.putAll(currentListedFiles);
	}

	private void notifyListenersOfNewEvent(Event event) {
		notifier.notifyListeners(event);
	}

 	boolean isFilesystemAccessible(){
		return isFilesystemUnaccessible;
	}
	boolean isFilesystemUnaccessible(){
		return !isFilesystemAccessible();
	}

	synchronized void waitForExecutionToStop() {
		// This method is called after the Timer has been canceled.
		// If there is an ongoing poll while timer is canceled, this 
		// method will block until the last poll has finished executing
		// (since both this and the run() methods are synchronized).
	}

}