package org.adp4j.spi;

import java.io.IOException;
import java.util.Set;

import org.adp4j.core.DirectoryPoller;
import org.adp4j.core.DirectoryPollerException;

/**
 * Implementations of this interface represents the directory to poll for 
 * file elements.
 *
 */
public interface PolledDirectory {
	
	/**
	 * Returns a snapshot of all current {@link FileElement}s in this directory.
	 * <p>
	 * Returning a {@code null} value will be treated the same as if an 
	 * {@link IOException} was thrown.
	 * <p>
	 * NOTE! </br>
	 * All files within a directory are expected to have unique names (i.e. 
	 * method {@link FileElement#getName()} is expected to return a name unique
	 * among all files within this directory).
	 * 
	 * @return a list of {@link FileElement}s in this directory
	 * 
	 * @throws IOException if not possible to list files in this directory, due
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
	Set<FileElement> listFiles() throws IOException;
	
	/**
	 * It is recommended to implement this method if clients will remove a {@link PolledDirectory}
	 * from the {@link DirectoryPoller}.
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * It is recommended to implement this method if clients will remove a {@link PolledDirectory}
	 * from the {@link DirectoryPoller}.
	 * 
	 */
	@Override
	public int hashCode();
}
