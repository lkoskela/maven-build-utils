package com.lassekoskela.maven.timeline;

public class TimelineExportException extends RuntimeException {

	private static final long serialVersionUID = -3189917456725342101L;

	public TimelineExportException() {
		super();
	}

	public TimelineExportException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimelineExportException(String message) {
		super(message);
	}

	public TimelineExportException(Throwable cause) {
		super(cause);
	}

}
