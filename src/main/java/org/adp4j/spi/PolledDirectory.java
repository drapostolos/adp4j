package org.adp4j.spi;

import java.io.IOException;
import java.util.List;

public interface PolledDirectory {
	List<FileObject> listFiles() throws IOException;
}
