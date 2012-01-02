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
	public void mojoSkipped(ExecutionEvent event) {
		log(event);
	}

	@Override
	public void mojoStarted(ExecutionEvent event) {
		MojoExecution mojo = event.getMojoExecution();
		String phase = mojo.getLifecyclePhase();
		log.start(phase, mojo.getGroupId(), mojo.getArtifactId(),
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

	@Override
	public void projectSucceeded(ExecutionEvent event) {
		projectEnded(event);
	}

	@Override
	public void projectFailed(ExecutionEvent event) {
		projectEnded(event);
	}

	private void mojoEnded(ExecutionEvent event) {
		MojoExecution mojo = event.getMojoExecution();
		String phase = mojo.getLifecyclePhase();
		log.end(phase, mojo.getGroupId(), mojo.getArtifactId(), mojo.getGoal());
	}

	private void projectEnded(ExecutionEvent event) {
		log.report();
	}

	private void log(ExecutionEvent event) {
		MojoExecution mojo = event.getMojoExecution();
		String phase = mojo.getLifecyclePhase();
		log.end(phase, mojo.getGroupId(), mojo.getArtifactId(), mojo.getGoal());
	}
}