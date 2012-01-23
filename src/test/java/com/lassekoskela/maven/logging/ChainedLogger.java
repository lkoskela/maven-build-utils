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

	@Override
	public String destination() {
		StringBuilder s = new StringBuilder();
		for (Log log : logs) {
			if (s.length() > 0) {
				s.append(", ");
			}
			s.append(log.destination());
		}
		return "chain(" + s.toString() + ")";
	}
}
