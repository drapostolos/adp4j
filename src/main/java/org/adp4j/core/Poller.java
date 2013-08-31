package org.adp4j.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.adp4j.spi.FileObject;
import org.adp4j.spi.PolledDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Poller implements Callable<Object>{
	private static Logger logger = LoggerFactory.getLogger(PollerTask.class);
	private final Map<FileObject, Long> currentListedFiles = new LinkedHashMap<FileObject, Long>();
	private final Map<FileObject, Long> previousListedFiles = new LinkedHashMap<FileObject, Long>();
	private final List<FileObject> modifiedFiles = new ArrayList<FileObject>();
	private final DirectoryPoller dp;
	private final PolledDirectory directory;
	private final FileFilter filter;
	private final ListenerNotifier notifier;
	private final boolean fileAddedEventEnabledForInitialContent;
	private boolean isSecondPollCycleOrLater = false;
	private boolean isFilesystemUnaccessible = true;
	private HashMapDiffer<FileObject, Long> mapComparer;
	
	public Poller(DirectoryPoller dp, PolledDirectory directory) {
		this.dp = dp;
		this.directory = directory;
		this.filter = dp.getFileFilter();
		this.notifier = dp.notifier;
		this.fileAddedEventEnabledForInitialContent = dp.fileAddedEventEnabledForInitialContent;
	}
	
	public Object call() {
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
				notifier.notifyListeners(new InitialContentEvent(dp, directory, new HashSet<FileObject>(currentListedFiles.keySet())));
				isSecondPollCycleOrLater = true;
			}
		}
		return null;
	}

	boolean isSecondPollCycleOrLater(){
		return isSecondPollCycleOrLater;
	}
	boolean isFirstPollCycle(){
		return !isSecondPollCycleOrLater();
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
				notifier.notifyListeners(new IoErrorCeasedEvent(dp, directory));
				isFilesystemUnaccessible = true;
			}
			currentListedFiles.clear();
			currentListedFiles.putAll(temp);
		} catch (IOException e) {
			if(isFilesystemAccessible()){
				isFilesystemUnaccessible = false;
				notifier.notifyListeners(new IoErrorRaisedEvent(dp, directory, e));
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
	
 	boolean isFilesystemAccessible(){
		return isFilesystemUnaccessible;
	}
	boolean isFilesystemUnaccessible(){
		return !isFilesystemAccessible();
	}

	private void setComparerForCurrentVersusPreviousListedFiles(){
		mapComparer = new HashMapDiffer<FileObject, Long>(previousListedFiles, currentListedFiles);
	}

	private boolean isDirectoryModified() {
		return !modifiedFiles.isEmpty() || mapComparer.hasDiff();
	}

	private void notifyListenersWithRemovedAddedModifiedFiles(){
		for(FileObject file : mapComparer.getRemoved().keySet()){
			notifier.notifyListeners(new FileRemovedEvent(dp, directory, file));
		}
		
		for(FileObject file : mapComparer.getAdded().keySet()){
			notifier.notifyListeners(new FileAddedEvent(dp, directory, file));
		}
		for(FileObject file : modifiedFiles){
			notifier.notifyListeners(new FileModifiedEvent(dp, directory, file));
		}
	}

	private void copyCurrentListedFilesToPrevious(){
		previousListedFiles.clear();
		previousListedFiles.putAll(currentListedFiles);
	}



}
