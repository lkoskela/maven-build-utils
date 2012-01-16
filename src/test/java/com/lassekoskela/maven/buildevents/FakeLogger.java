package com.lassekoskela.maven.buildevents;

import org.codehaus.plexus.logging.Logger;

public final class FakeLogger implements Logger {
	private StringBuilder log;
	private String name;
	private int threshold;

	public FakeLogger() {
		log = new StringBuilder();
		name = "fakelogger";
		threshold = LEVEL_INFO;
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
	public void setThreshold(int level) {
		switch (level) {
		case Logger.LEVEL_DEBUG:
		case Logger.LEVEL_INFO:
		case Logger.LEVEL_WARN:
		case Logger.LEVEL_ERROR:
		case Logger.LEVEL_FATAL:
		case Logger.LEVEL_DISABLED:
			this.threshold = level;
		default:
			throw new IllegalArgumentException("Not a valid threshold: "
					+ level);
		}
	}

	@Override
	public int getThreshold() {
		return threshold;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Logger getChildLogger(String name) {
		return null;
	}

	@Override
	public boolean isWarnEnabled() {
		return threshold <= LEVEL_WARN;
	}

	@Override
	public boolean isInfoEnabled() {
		return threshold <= LEVEL_INFO;
	}

	@Override
	public boolean isFatalErrorEnabled() {
		return threshold <= LEVEL_FATAL;
	}

	@Override
	public boolean isErrorEnabled() {
		return threshold <= LEVEL_ERROR;
	}

	@Override
	public boolean isDebugEnabled() {
		return threshold <= LEVEL_DEBUG;
	}

	@Override
	public void warn(String msg, Throwable e) {
		write("warn", msg);
	}

	@Override
	public void warn(String msg) {
		write("warn", msg);
	}

	@Override
	public void info(String msg, Throwable e) {
		write("info", msg);
	}

	@Override
	public void info(String msg) {
		write("info", msg);
	}

	@Override
	public void fatalError(String msg, Throwable e) {
		write("fatal", msg);
	}

	@Override
	public void fatalError(String msg) {
		write("fatal", msg);
	}

	@Override
	public void error(String msg, Throwable e) {
		write("error", msg);
	}

	@Override
	public void error(String msg) {
		write("error", msg);
	}

	@Override
	public void debug(String msg, Throwable e) {
		write("debug", msg);
	}

	@Override
	public void debug(String msg) {
		write("debug", msg);
	}
}