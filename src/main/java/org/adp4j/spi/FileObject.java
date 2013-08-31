package org.adp4j.spi;

import java.io.IOException;

import org.adp4j.core.DirectoryPollerException;

public abstract class FileObject {

	/**
	 * Returns the time when this {@link FileObject} was last modified.
	 * 
	 * @return A long value representing the time this {@link FileObject} 
	 * was last modified. 
	 * 
	 * @throws IOException if not possible to fetch the last modified time.
	 * 
	 * @throws DirectoryPollerException if underlying {@link PolledDirectory}
	 * implementation detects an error (which is not an IOException). This will
	 * cause the Directory-Poller to silently skip this poll and wait for 
	 * next poll.
	 * 
	 * @throws RuntimeException if underlying {@link PolledDirectory}
	 * implementation crashes unexpectedly. This will cause the Directory-Poller
	 * to log an error message (along with the RuntimeException) and wait for next
	 * poll.
	 */
	public abstract long lastModified() throws IOException;
	
	/**
	 * This method returns true, if this {@link FileObject} represents 
	 * a directory, otherwise false.
	 * 
	 * @return true if this {@link FileObject} instance represents a 
	 * directory, otherwise false.
	 * 
	 * @throws DirectoryPollerException if underlying {@PolledDirectory}
	 * implementation detects an error (which is not an IOException).This will
	 * cause the Directory-Poller to silently skip this poll and wait for 
	 * next poll.
	 * 
	 * @throws RuntimeException if underlying {@link PolledDirectory}
	 * implementation crashes unexpectedly. This will cause the Directory-Poller
	 * to log an error message (along with the RuntimeException) and wait for next
	 * poll.
	 */
	public abstract boolean isDirectory();
	
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
	public abstract String getName();
	public abstract boolean equals(Object obj);
	public abstract int hashCode();
}
