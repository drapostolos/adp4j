package org.adp4j.core;

import java.io.IOException;

import org.adp4j.spi.FileObject;

public class StubbedFileObject extends FileObject{
	private final String name;
	private final long lastModified;
	
	/*
	 * Make a stub so equals/hashCode methods can be implemented correctly.
	 */
	public StubbedFileObject(String name, long lastModified) {
		this.name = name;
		this.lastModified = lastModified;
	}

	@Override
	public long lastModified() throws IOException {
		return lastModified;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		StubbedFileObject other = (StubbedFileObject) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
		.append(hashCode()).append(":")
		.append(name).append(":")
		.append(String.valueOf(lastModified))
		.toString();
	}

}
