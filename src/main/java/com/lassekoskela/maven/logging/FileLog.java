package com.lassekoskela.maven.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLog implements Log {

	private static final String LINEFEED = System.getProperty("line.separator");
	private final File output;
	private boolean hasWrittenToLog;

	public FileLog(File output) {
		this.output = output;
		this.hasWrittenToLog = false;
	}

	@Override
	public void info(String message) {
		write("info", message);
	}

	public String destination() {
		return output.getPath();
	}

	private synchronized void write(String level, String message) {
		mkdirs(output.getParentFile());
		try {
			FileWriter w = new FileWriter(output, hasWrittenToLog);
			w.append("[").append(level.toUpperCase()).append("] ");
			w.append(message).append(LINEFEED);
			w.flush();
			w.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			hasWrittenToLog = true;
		}
	}

	private void mkdirs(File path) {
		if (!path.exists()) {
			path.mkdirs();
		}
	}
}
