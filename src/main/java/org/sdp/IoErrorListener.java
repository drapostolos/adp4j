package org.sdp;



public interface IoErrorListener extends Listener{
	void ioErrorRaised(IoErrorRaisedEvent event);
	void ioErrorCeased(IoErrorCeasedEvent event);
}
