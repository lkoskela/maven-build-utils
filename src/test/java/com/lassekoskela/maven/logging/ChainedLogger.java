package com.lassekoskela.maven.logging;

import org.codehaus.plexus.logging.Logger;

public class ChainedLogger implements Logger {

	private final Logger[] loggers;

	public ChainedLogger(Logger... loggers) {
		this.loggers = loggers;
	}

	@Override
	public Logger getChildLogger(String name) {
		return null;
	}

	@Override
	public void debug(String msg, Throwable e) {
		for (Logger logger : loggers) {
			logger.debug(msg, e);
		}
	}

	@Override
	public void error(String msg, Throwable e) {
		for (Logger logger : loggers) {
			logger.error(msg, e);
		}
	}

	@Override
	public void fatalError(String msg, Throwable e) {
		for (Logger logger : loggers) {
			logger.fatalError(msg, e);
		}
	}

	@Override
	public void info(String msg, Throwable e) {
		for (Logger logger : loggers) {
			logger.info(msg, e);
		}
	}

	@Override
	public void warn(String msg, Throwable e) {
		for (Logger logger : loggers) {
			logger.warn(msg, e);
		}
	}

	@Override
	public void debug(String msg) {
		for (Logger logger : loggers) {
			logger.debug(msg);
		}
	}

	@Override
	public void error(String msg) {
		for (Logger logger : loggers) {
			logger.error(msg);
		}
	}

	@Override
	public void fatalError(String msg) {
		for (Logger logger : loggers) {
			logger.fatalError(msg);
		}
	}

	@Override
	public void info(String msg) {
		for (Logger logger : loggers) {
			logger.info(msg);
		}
	}

	@Override
	public void warn(String msg) {
		for (Logger logger : loggers) {
			logger.warn(msg);
		}
	}

	@Override
	public String getName() {
		return "chainedlogger";
	}

	@Override
	public int getThreshold() {
		for (Logger logger : loggers) {
			return logger.getThreshold();
		}
		return LEVEL_DEBUG;
	}

	@Override
	public boolean isDebugEnabled() {
		for (Logger logger : loggers) {
			return logger.isDebugEnabled();
		}
		return false;
	}

	@Override
	public boolean isErrorEnabled() {
		for (Logger logger : loggers) {
			return logger.isErrorEnabled();
		}
		return false;
	}

	@Override
	public boolean isFatalErrorEnabled() {
		for (Logger logger : loggers) {
			return logger.isFatalErrorEnabled();
		}
		return false;
	}

	@Override
	public boolean isInfoEnabled() {
		for (Logger logger : loggers) {
			return logger.isInfoEnabled();
		}
		return false;
	}

	@Override
	public boolean isWarnEnabled() {
		for (Logger logger : loggers) {
			return logger.isWarnEnabled();
		}
		return false;
	}

	@Override
	public void setThreshold(int level) {
		for (Logger logger : loggers) {
			logger.setThreshold(level);
		}
	}
}
