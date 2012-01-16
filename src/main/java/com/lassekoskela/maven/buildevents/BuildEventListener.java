package com.lassekoskela.maven.buildevents;

import org.apache.maven.execution.AbstractExecutionListener;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;

public class BuildEventListener extends AbstractExecutionListener {
	private final BuildEventLog log;

	public BuildEventListener(BuildEventLog buildEventLog) {
		this.log = buildEventLog;
	}

	@Override
	public void sessionEnded(ExecutionEvent event) {
		log.report();
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