package org.adp4j.local.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.adp4j.spi.FileElement;
import org.adp4j.spi.PolledDirectory;

public final class JavaIoFileAdapter implements FileElement, PolledDirectory{
	private final File file;

	public JavaIoFileAdapter(File file) {
		if(file == null){
			throw new NullPointerException("null argument not allowed!");
		}
		this.file = file;
	}

	@Override
	public long lastModified() throws IOException {
		long lastModified = file.lastModified();
		if(lastModified == 0L){
			String message = 
					"Unknown I/O error occured when retriveing lastModified "	+ 
					"attribute for file '%s'.";
			throw new IOException(String.format(message, file));
		}
		return lastModified;
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaIoFileAdapter other = (JavaIoFileAdapter) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}

	@Override
	public Set<FileElement> listFiles() throws IOException {
		Set<FileElement> result = new LinkedHashSet<FileElement>();
		File[] files = file.listFiles();
		if(files == null){
			String message = "Unknown I/O error when listing files in directory '%s'.";
			throw new IOException(String.format(message, file));
		}
		for(File child : file.listFiles()){
			result.add(new JavaIoFileAdapter(child));
		}
		return result;
	}

	@Override
	public String toString() {
		return file.toString();
	}

}
