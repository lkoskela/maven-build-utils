package com.lassekoskela.maven.buildevents;

import com.lassekoskela.maven.logging.Log;

public final class FakeLogger implements Log {
	private StringBuilder log;

	public FakeLogger() {
		log = new StringBuilder();
	}

	@Override
	public String destination() {
		return "fake";
	}

	/**
	 * Returns the complete output written to this log so far.
	 */
	public String output() {
		return log.toString();
	}

	protected synchronized void write(String level, String msg) {
		if (log.length() > 0) {
			log.append("\n");
		}
		log.append("[" + level.toUpperCase() + "] ").append(msg);
	}

	@Override
	public void info(String message) {
		write("info", message);
	}
}