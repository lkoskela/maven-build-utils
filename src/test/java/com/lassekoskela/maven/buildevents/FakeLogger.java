package com.lassekoskela.maven.buildevents;

public final class FakeLogger implements Log {
	private StringBuilder log;

	public FakeLogger() {
		log = new StringBuilder();
	}

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