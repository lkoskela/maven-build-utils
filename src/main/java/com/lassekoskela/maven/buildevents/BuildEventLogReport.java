package com.lassekoskela.maven.buildevents;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.lang.String.format;
import static org.hamcrest.Matchers.allOf;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;

import com.lassekoskela.maven.logging.Log;
import com.lassekoskela.time.Duration;

class BuildEventLogReport {
	private final Log log;
	private final List<BuildStep> steps;

	public BuildEventLogReport(Log log) {
		this(log, new ArrayList<BuildStep>());
	}

	public BuildEventLogReport(Log log, List<BuildStep> steps) {
		this.log = log;
		this.steps = steps;
	}

	public void report() {
		printHeader();
		reportBuildSteps(steps);
		printFooter();
	}

	private void reportBuildSteps(List<BuildStep> steps) {
		String currentProject = "";
		String currentPhase = "";
		long totalDuration = totalDuration();
		for (BuildStep buildStep : steps) {
			reportBuildStep(currentProject, currentPhase, buildStep,
					totalDuration);
			currentPhase = buildStep.phase;
			currentProject = buildStep.project;
		}
	}

	private void reportBuildStep(String currentProject, String currentPhase,
			BuildStep buildStep, long totalDuration) {
		String project = buildStep.project;
		String phase = buildStep.phase;
		long phaseDuration = totalDurationOfPhase(project, phase);
		long projectDuration = totalDurationOfProject(project);
		long percentageOfPhase = (long) buildStep.duration().percentageOf(
				phaseDuration);
		if (project != null && !project.equals(currentProject)) {
			log.info("");
			reportProjectStatistics(project, totalDuration, projectDuration);
		}
		if (phase != null && !phase.equals(currentPhase)) {
			reportPhaseStatistics(phase, projectDuration, phaseDuration);
		}
		reportGoalStatistics(buildStep, percentageOfPhase);
	}

	private void reportGoalStatistics(BuildStep buildStep, long percentage) {
		String goal = buildStep.artifactId + ":" + buildStep.goal;
		double seconds = buildStep.duration().inSeconds();
		log.info(format("    %-54s %7.1fs %3s%%", goal, seconds, percentage));
	}

	private void reportPhaseStatistics(String phase, long totalDuration,
			long totalDurationOfPhase) {
		Duration phaseDuration = new Duration(totalDurationOfPhase);
		long percentage = (long) phaseDuration.percentageOf(totalDuration);
		double seconds = phaseDuration.inSeconds();
		log.info(format("%-58s %7.1fs %3s%%", "  " + phase, seconds, percentage));
	}

	private void reportProjectStatistics(String project, long totalDuration,
			long totalDurationOfProject) {
		Duration projectDuration = new Duration(totalDurationOfProject);
		long percentage = (long) projectDuration.percentageOf(totalDuration);
		double seconds = projectDuration.inSeconds();
		log.info(format("%-58s %7.1fs %3s%%", "*" + project, seconds,
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

	private void printFooter() {
		log.info("------------------------------------------------------------------------");
	}

	private void printHeader() {
		log.info("------------------------- BUILD STEP DURATIONS -------------------------");
		log.info("PROJECT                                                    DURATION     ");
		log.info("| PHASE                                                       PERCENTAGE");
		log.info("| | GOAL                                                          |    |");
		log.info("| | |                                                             |    |");
	}
}