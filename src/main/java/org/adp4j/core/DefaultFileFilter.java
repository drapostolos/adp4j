package org.adp4j.core;

import org.adp4j.spi.FileElement;

final class DefaultFileFilter implements FileFilter{

	@Override
	public boolean accept(FileElement file) {
		return true;
	}

}
