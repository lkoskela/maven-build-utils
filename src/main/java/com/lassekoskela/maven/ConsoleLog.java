package com.lassekoskela.maven;

import org.codehaus.plexus.logging.Logger;

import com.lassekoskela.maven.buildevents.Log;

public class ConsoleLog implements Log {

	private final Logger logger;

	public ConsoleLog(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void info(String message) {
		logger.info(message);
	}
}
