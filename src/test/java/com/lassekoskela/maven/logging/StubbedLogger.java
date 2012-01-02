package com.lassekoskela.maven.logging;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

public class StubbedLogger extends AbstractLogger {
	StubbedLogger(int threshold, String name) {
		super(threshold, name);
	}

	public StubbedLogger() {
		super(LEVEL_DEBUG, "stubbedlogger");
	}

	@Override
	public void warn(String msg, Throwable e) {
		System.out.println("[WARN] " + msg);
	}

	@Override
	public void info(String msg, Throwable e) {
		System.out.println("[INFO] " + msg);
	}

	@Override
	public Logger getChildLogger(String name) {
		return null;
	}

	@Override
	public void fatalError(String msg, Throwable e) {
		System.out.println("[FATAL] " + msg);
	}

	@Override
	public void error(String msg, Throwable e) {
		System.out.println("[ERROR] " + msg);
	}

	@Override
	public void debug(String msg, Throwable e) {
		System.out.println("[DEBUG] " + msg);
	}
}