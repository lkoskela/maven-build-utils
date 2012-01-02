package com.lassekoskela.maven.buildevents;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.logging.Logger;

public class BuildEventLog {
	private final Logger logger;
	private final List<BuildStep> steps;
	private BuildStep latestStep;

	public BuildEventLog(Logger logger) {
		this.logger = logger;
		this.steps = new ArrayList<BuildStep>();
	}

	public void start(String phase, String groupId, String artifactId,
			String goal) {
		latestStep = new BuildStep(phase, groupId, artifactId, goal);
		latestStep.start();
	}

	public void end(String phase, String groupId, String artifactId, String goal) {
		latestStep.end();
		steps.add(latestStep);
	}

	public void report() {
		createReport().report();
	}

	public long totalDuration() {
		return createReport().totalDuration();
	}

	public long totalDurationOfPhase(String phase) {
		return createReport().totalDurationOfPhase(phase);
	}

	protected BuildEventLogReport createReport(Logger logger,
			List<BuildStep> steps) {
		BuildEventLogReport report = new BuildEventLogReport(logger);
		report.add(steps);
		return report;
	}

	private BuildEventLogReport createReport() {
		return createReport(logger, steps);
	}
}
