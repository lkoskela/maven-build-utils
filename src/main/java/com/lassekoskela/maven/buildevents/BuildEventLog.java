package com.lassekoskela.maven.buildevents;

import java.util.ArrayList;
import java.util.List;

import com.lassekoskela.maven.logging.Log;

public class BuildEventLog {
	private final Log logger;
	private final List<BuildStep> steps;
	private BuildStep latestStep;

	public BuildEventLog(Log logger) {
		this.logger = logger;
		this.steps = new ArrayList<BuildStep>();
	}

	public void start(String project, String phase, String groupId,
			String artifactId, String goal) {
		latestStep = new BuildStep(project, phase, groupId, artifactId, goal);
		latestStep.start();
	}

	public void end(String project, String phase, String groupId,
			String artifactId, String goal) {
		latestStep.end();
		steps.add(latestStep);
	}

	public void report() {
		createReport().report();
	}

	public long totalDuration() {
		return createReport().totalDuration();
	}

	public long totalDurationOfProject(String project) {
		return createReport().totalDurationOfProject(project);
	}

	public long totalDurationOfPhase(String phase) {
		return createReport().totalDurationOfPhase(phase);
	}

	public long totalDurationOfPhase(String project, String phase) {
		return createReport().totalDurationOfPhase(project, phase);
	}

	protected BuildEventLogReport createReport(Log log, List<BuildStep> steps) {
		BuildEventLogReport report = new BuildEventLogReport(log);
		report.add(steps);
		return report;
	}

	private BuildEventLogReport createReport() {
		return createReport(logger, steps);
	}
}
