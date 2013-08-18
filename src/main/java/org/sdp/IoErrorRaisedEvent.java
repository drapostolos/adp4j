package org.sdp;

import java.io.IOException;

public class IoErrorRaisedEvent extends Event {
	private final IOException ioException;

	public IoErrorRaisedEvent(DirectoryPoller dp, IOException e) {
		super(dp);
		ioException = e;
	}
	
	public IOException getIoException() {
		return ioException;
	}

}
