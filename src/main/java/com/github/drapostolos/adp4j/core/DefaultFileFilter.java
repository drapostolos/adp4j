package com.github.drapostolos.adp4j.core;

import com.github.drapostolos.adp4j.spi.FileElement;

final class DefaultFileFilter implements FileFilter{

	@Override
	public boolean accept(FileElement file) {
		return true;
	}

}
