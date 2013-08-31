package org.adp4j.core;

import org.adp4j.spi.FileObject;


/**
 * Instances of this interface is passed to the {@link MonitoredFileParameterized#listFiles(FileFilter)} 
 * method. It is used to filter which instances of {@link MonitoredFileParameterized} to list or not to list
 * in a monitored directory.
 */
public interface FileFilter {
	/**
	 * @param file
	 * @return true if the given <i>file</i> is accepted by this filter or not.
	 */
    boolean accept(FileObject file);

}
