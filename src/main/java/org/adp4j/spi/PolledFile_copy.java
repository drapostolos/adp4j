package org.adp4j.spi;

import java.io.IOException;
import java.util.List;

import org.adp4j.core.FileFilter;


/**
 * Implementations of this interface must provide the possibility to
 * list files and their lastModified dates in a directory.
 * <p>
 * NOTE! It is mandatory to override {@link Object#equals(Object)} and
 * {@link Object#hashCode()} methods in an implementation of {@link PolledFile_copy}.
 *  
 */
public interface PolledFile_copy {
	/**
	 * Returns the time when this file was last modified.
	 * <p>
	 * Returning the value "0L" will result in a "IoErrorRaised" event. 
	 * 
	 * @return A long value representing the time the file was last modified. 
	 * 
	 * @throws IOException if not possible to fetch the last modified time.
	 * Will result in a "IoErrorRaised" event.
	 * 
	 * @throws DirectoryMonitorException if underlying {@link PolledFile_copy}
	 * implementation detects an error (which is not an IOException). Will result in the 
	 * Directory-Monitor to silently skip this poll and wait for next poll.
	 * 
	 * @throws RuntimeException if underlying {@link PolledFile_copy}
	 * implementation crashes unexpectedly. Will result in the Directory-Monitor to 
	 * log an error message and wait for next poll.
	 */
	long lastModified() throws IOException;
	
	/**
	 * Returns the name of this file.
	 * 
	 * @return name of file
	 * 
	 * @throws DirectoryMonitorException if underlying {@link PolledFile_copy}
	 * implementation detects an error (which is not an IOException). Will result in the 
	 * Directory-Monitor to silently skip this poll and wait for next poll.
	 * 
	 * @throws RuntimeException if underlying {@link PolledFile_copy}
	 * implementation crashes unexpectedly. Will result in the Directory-Monitor to 
	 * log an error message and wait for next poll.
	 */
	String getName();
	
	/**
	 * Returns a List of all {@link PolledFile_copy} contained in 
	 * this directory, which matches the given {@link FileFilter}.
	 * <p>
	 * It is up to the implementation to check if this 
	 * is a directory, and throw an appropriate exception if it's not 
	 * (with appropriate error message). 
	 * <p>
	 * Returning a null value will result in a "IoErrorRaised" event.
	 * 
	 * @param fileFilter {@link FileFilter} implementation
	 * 
	 * @return a list of {@link PolledFile_copy}
	 * 
	 * @throws IOException if not possible to list files. Will result in a 
	 * "IoErrorRaised" event.
	 * 
	 * @throws DirectoryMonitorException if underlying {@link PolledFile_copy}
	 * implementation detects an error (which is not an IOException). Will result in the 
	 * Directory-Monitor to silently skip this poll and wait for next poll.
	 * 
	 * @throws RuntimeException if underlying {@link PolledFile_copy}
	 * implementation crashes unexpectedly. Will result in the Directory-Monitor to 
	 * log an error message and wait for next poll.
	 */ 
	List<PolledFile_copy> listFiles() throws IOException;
	
	/**
	 * This method shall return true, if this instance is considered 
	 * a directory (i.e can contain files).
	 * 
	 * @return true if this is considered a directory, otherwise false.
	 * 
	 * @throws DirectoryMonitorException if underlying {@link PolledFile_copy}
	 * implementation detects an error (which is not an IOException). Will result in the 
	 * Directory-Monitor to silently skip this poll and wait for next poll.
	 * 
	 * @throws RuntimeException if underlying {@link PolledFile_copy}
	 * implementation crashes unexpectedly. Will result in the Directory-Monitor to 
	 * log an error message and wait for next poll.
	 */
	boolean isDirectory();
	

}
