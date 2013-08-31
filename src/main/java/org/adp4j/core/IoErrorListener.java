package org.adp4j.core;



public interface IoErrorListener extends Listener{
	void ioErrorRaised(IoErrorRaisedEvent event);
	void ioErrorCeased(IoErrorCeasedEvent event);
}
