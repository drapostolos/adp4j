package org.sdp;

import org.sdp.spi.FileObject;



final class DefaultFileFilter implements FileFilter{

	/**
	 * A default {@link FileFilter} that accepts all {@link MonitoredFileParameterized} instances.
	 */
	@Override
	public boolean accept(FileObject file) {
		return true;
	}

}
