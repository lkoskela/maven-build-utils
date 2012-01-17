package com.lassekoskela.maven.buildevents;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.maven.execution.AbstractExecutionListener;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;

public class BuildEventListener extends AbstractExecutionListener {
	private final BuildEventLog log;
	private File buildDir;

	public BuildEventListener(BuildEventLog buildEventLog) {
		this.log = buildEventLog;
	}

	@Override
	public void sessionStarted(ExecutionEvent event) {
		super.sessionStarted(event);
		buildDir = new File(event.getProject().getBuild().getDirectory());
	}

	@Override
	public void sessionEnded(ExecutionEvent event) {
		if (!buildDir.exists()) {
			buildDir.mkdirs();
		}
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(buildDir, "durations.log"));
			log.report(writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		super.sessionEnded(event);
	}

	@Override
	public void mojoSkipped(ExecutionEvent event) {
		log(event);
	}

	@Override
	public void mojoStarted(ExecutionEvent event) {
		MojoExecution mojo = event.getMojoExecution();
		String phase = mojo.getLifecyclePhase();
		String project = event.getProject().getArtifactId();
		log.start(project, phase, mojo.getGroupId(), mojo.getArtifactId(),
				mojo.getGoal());
	}

	@Override
	public void mojoSucceeded(ExecutionEvent event) {
		mojoEnded(event);
	}

	@Override
	public void mojoFailed(ExecutionEvent event) {
		mojoEnded(event);
	}

	private void mojoEnded(ExecutionEvent event) {
		MojoExecution mojo = event.getMojoExecution();
		String phase = mojo.getLifecyclePhase();
		String project = event.getProject().getArtifactId();
		log.end(project, phase, mojo.getGroupId(), mojo.getArtifactId(),
				mojo.getGoal());
	}

	private void log(ExecutionEvent event) {
		MojoExecution mojo = event.getMojoExecution();
		String phase = mojo.getLifecyclePhase();
		String project = event.getProject().getArtifactId();
		log.end(project, phase, mojo.getGroupId(), mojo.getArtifactId(),
				mojo.getGoal());
	}
}