package com.github.drapostolos.adp4j.spi;

import java.io.IOException;

import com.github.drapostolos.adp4j.core.DirectoryPoller;
import com.github.drapostolos.adp4j.core.DirectoryPollerException;

/**
 * Implementations of this interface represent a file element in the polled
 * directory.
 *
 */
public interface FileElement {

	/**
	 * Returns the time when this {@link FileElement} was last modified.
	 * <p>
	 * Returning a {@code 0L} value will be treated the same as if an 
	 * {@link IOException} was thrown.
	 * 
	 * @return A long value representing the time this {@link FileElement} 
	 * was last modified. 
	 * 
	 * @throws IOException if not possible to fetch the last modified time, due
	 * to I/O error.
	 * 
	 * @throws DirectoryPollerException if any other error (besides 
	 * {@link IOException}) is detected. This will cause the Directory-Poller 
	 * to silently skip this poll-cycle and wait for next poll-cycle.
	 * 
	 * @throws RuntimeException if any unpredictable error occurs. This will 
	 * cause the Directory-Poller to log an error message (along with the causing 
	 * RuntimeException) and wait for next poll-cycle.
	 */
	long lastModified() throws IOException;
	
	/**
	 * This method returns true, if this {@link FileElement} represents 
	 * a directory, otherwise false.
	 * <p>
	 * This method is optional to implement.
	 * 
	 * @return true if this {@link FileElement} instance represents a 
	 * directory, otherwise false.
	 */
	boolean isDirectory();
	
	/**
	 * Returns the name of this {@link FileElement}.
	 * <p>
	 * NOTE! </br>
	 * All files within a directory are expected to have unique names.
	 * 
	 * @return name of {@link FileElement}.
	 * 
	 * @throws DirectoryPollerException if any error is detected. This will 
	 * cause the Directory-Poller to silently skip this poll-cycle and wait for next poll-cycle.
	 * 
	 * @throws RuntimeException if any unpredictable error occurs. This will 
	 * cause the {@link DirectoryPoller} to log an error message (along with the causing 
	 * RuntimeException) and wait for next poll-cycle.
	 */
	String getName();
}
