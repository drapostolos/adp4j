package org.sdp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sdp.spi.PolledDirectory;
import org.sdp.spi.FileObject;

public class PolledJavaFile extends FileObject implements PolledDirectory{
	private final File javaFile;
	
	public PolledJavaFile(File file) {
		this.javaFile = file;
	}

	@Override
	public long lastModified() throws IOException {
		return javaFile.lastModified();
	}

	@Override
	public String getName() {
		return javaFile.getName();
	}

	@Override
	public List<FileObject> listFiles() throws IOException {
		List<FileObject> result = new ArrayList<FileObject>();
		for(File f : javaFile.listFiles()){
			FileObject polledFile = new PolledJavaFile(f);
			result.add(polledFile);
		}
		return result;
	}

	@Override
	public boolean isDirectory() {
		return javaFile.isDirectory();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((javaFile == null) ? 0 : javaFile.hashCode());
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
		PolledJavaFile other = (PolledJavaFile) obj;
		if (javaFile == null) {
			if (other.javaFile != null)
				return false;
		} else if (!javaFile.equals(other.javaFile))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return javaFile.toString();
	}

}
