package com.lassekoskela.maven.logging;

import org.codehaus.plexus.logging.Logger;


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
