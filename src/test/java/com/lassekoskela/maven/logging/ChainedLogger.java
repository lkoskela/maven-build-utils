package com.lassekoskela.maven.logging;


public class ChainedLogger implements Log {

	private final Log[] logs;

	public ChainedLogger(Log... logs) {
		this.logs = logs;
	}

	@Override
	public void info(String msg) {
		for (Log log : logs) {
			log.info(msg);
		}
	}
}
