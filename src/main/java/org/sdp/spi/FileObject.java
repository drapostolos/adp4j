package org.sdp.spi;

import java.io.IOException;

public abstract class FileObject {
	public abstract long lastModified() throws IOException;
	public abstract boolean isDirectory();
	public abstract String getName();
	public abstract boolean equals(Object obj);
	public abstract int hashCode();
}
