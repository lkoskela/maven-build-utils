package com.lassekoskela.maven.buildevents;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.lang.String.format;
import static org.hamcrest.Matchers.allOf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;

import com.lassekoskela.time.Duration;

class BuildEventLogReport {
	private final List<BuildStep> steps;

	public BuildEventLogReport() {
		this(new ArrayList<BuildStep>());
	}

	public BuildEventLogReport(List<BuildStep> steps) {
		this.steps = steps;
	}

	public void report(PrintWriter out) {
		try {
			String currentProject = "";
			String currentPhase = "";
			long totalDuration = totalDuration();
			out.println("---------------------- BUILD STEP DURATIONS ------------------------");
			for (BuildStep buildStep : steps) {
				reportBuildStep(out, currentProject, currentPhase, buildStep,
						totalDuration);
				currentPhase = buildStep.phase;
				currentProject = buildStep.project;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void reportBuildStep(PrintWriter out, String currentProject,
			String currentPhase, BuildStep buildStep, long totalDuration)
			throws IOException {
		String project = buildStep.project;
		String phase = buildStep.phase;
		long phaseDuration = totalDurationOfPhase(project, phase);
		long projectDuration = totalDurationOfProject(project);
		long percentageOfPhase = (long) buildStep.duration().percentageOf(
				phaseDuration);
		if (project != null && !project.equals(currentProject)) {
			reportProjectStatistics(out, project, totalDuration,
					projectDuration);
		}
		if (phase != null && !phase.equals(currentPhase)) {
			reportPhaseStatistics(out, phase, projectDuration, phaseDuration);
		}
		reportGoalStatistics(out, buildStep, percentageOfPhase);
	}

	private void reportGoalStatistics(PrintWriter out, BuildStep buildStep,
			long percentage) throws IOException {
		String goal = buildStep.artifactId + ":" + buildStep.goal;
		double seconds = buildStep.duration().inSeconds();
		out.println(format("%-54s %7.1fs %3s%%", "    " + goal, seconds,
				percentage));
	}

	private void reportPhaseStatistics(PrintWriter out, String phase,
			long totalDuration, long totalDurationOfPhase) throws IOException {
		Duration phaseDuration = new Duration(totalDurationOfPhase);
		long percentage = (long) phaseDuration.percentageOf(totalDuration);
		double seconds = phaseDuration.inSeconds();
		out.println(format("%-54s %7.1fs %3s%%", "  " + phase, seconds,
				percentage));
	}

	private void reportProjectStatistics(PrintWriter out, String project,
			long totalDuration, long totalDurationOfProject) throws IOException {
		Duration projectDuration = new Duration(totalDurationOfProject);
		long percentage = (long) projectDuration.percentageOf(totalDuration);
		double seconds = projectDuration.inSeconds();
		out.println(format("%-54s %7.1fs %3s%%", "*" + project, seconds,
				percentage));
	}

	public long totalDurationOfPhase(String phase) {
		long total = 0;
		for (BuildStep e : filter(steps, phase(phase))) {
			total += e.duration().inMillis();
		}
		return total;
	}

	public long totalDurationOfPhase(final String project, final String phase) {
		long total = 0;
		for (BuildStep e : filter(steps, project(project), phase(phase))) {
			total += e.duration().inMillis();
		}
		return total;
	}

	private List<BuildStep> filter(List<BuildStep> steps,
			Matcher<BuildStep>... matchers) {
		return with(steps).clone().retain(allOf(matchers));
	}

	private Matcher<BuildStep> project(final String project) {
		return new FieldMatcher<BuildStep>("project", project);
	}

	private Matcher<BuildStep> phase(final String phase) {
		return new FieldMatcher<BuildStep>("phase", phase);
	}

	public long totalDurationOfProject(String project) {
		long total = 0;
		for (BuildStep e : filter(steps, project(project))) {
			total += e.duration().inMillis();
		}
		return total;
	}

	public long totalDuration() {
		long totalDuration = 0;
		for (BuildStep e : steps) {
			totalDuration += e.duration().inMillis();
		}
		return totalDuration;
	}

	public void add(List<BuildStep> steps) {
		this.steps.addAll(steps);
	}
}