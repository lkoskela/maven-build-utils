package com.lassekoskela.maven;

import java.io.File;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.ExecutionListener;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import com.lassekoskela.maven.buildevents.BuildEventListener;
import com.lassekoskela.maven.buildevents.BuildEventLog;
import com.lassekoskela.maven.buildevents.ExecutionListenerChain;
import com.lassekoskela.maven.logging.ConsoleLog;
import com.lassekoskela.maven.logging.FileLog;
import com.lassekoskela.maven.logging.Log;

@Component(role = AbstractMavenLifecycleParticipant.class, hint = "buildevents")
public class BuildEventsExtension extends AbstractMavenLifecycleParticipant {

	static final String OUTPUT_MODE = "duration.output";
	static final String OUTPUT_FILE = "duration.output.file";
	static final String DEFAULT_FILE_DESTINATION = "target/durations.log";

	@Requirement
	Logger logger;

	@Override
	public void afterProjectsRead(MavenSession session)
			throws MavenExecutionException {
		Log log = resolveLogDestination(session);
		BuildEventListener listener = createListener(log);
		registerExecutionListener(session, listener);
	}

	protected BuildEventListener createListener(Log log) {
		return new BuildEventListener(new BuildEventLog(log));
	}

	protected Log resolveLogDestination(MavenSession session) {
		String output = getProperty(session, OUTPUT_MODE, "console");
		if (output.equals("file")) {
			return createFileLog(session);
		}
		if (output.equals("console")) {
			return new ConsoleLog(logger);
		}
		logger.error("Invalid configuration: " + output);
		return new ConsoleLog(logger);
	}

	protected Log createFileLog(MavenSession session) {
		String file = getProperty(session, OUTPUT_FILE,
				DEFAULT_FILE_DESTINATION);
		if (new File(file).isAbsolute()) {
			return new FileLog(new File(file));
		}
		String buildDir = session.getExecutionRootDirectory();
		return new FileLog(new File(buildDir, file));
	}

	private String getProperty(MavenSession s, String key, String defaultValue) {
		return s.getUserProperties().getProperty(key, defaultValue);
	}

	protected void registerExecutionListener(MavenSession session,
			ExecutionListener listener) {
		MavenExecutionRequest request = session.getRequest();
		ExecutionListener original = request.getExecutionListener();
		ExecutionListener chain = new ExecutionListenerChain(original, listener);
		request.setExecutionListener(chain);
	}
}
