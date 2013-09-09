Abstract Directory Poller for Java (ADP4J)
=======

This library provides a Java SPI for polling directories on a REMOTE filesystem/ftp servers (etc) for changes (like file-Added/Removed/Modified).

The provider simply needs to implement these two interfaces:

public interface PolledDirectory {
	Set<FileElement> listFiles() throws IOException;
}

public interface FileElement {
	long lastModified() throws IOException;
	boolean isDirectory();
	String getName();
}
