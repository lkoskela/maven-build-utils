package com.lassekoskela.maven.buildevents;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.logging.Logger;

import com.lassekoskela.time.Duration;

class BuildEventLogReport {

	private final Logger logger;
	private final List<BuildStep> steps;

	public BuildEventLogReport(Logger logger) {
		this(logger, new ArrayList<BuildStep>());
	}

	public BuildEventLogReport(Logger logger, List<BuildStep> steps) {
		this.logger = logger;
		this.steps = steps;
	}

	public void report() {
		String currentPhase = "";
		long totalDuration = totalDuration();
		logger.info("----- BUILD STEP DURATIONS -----------------");
		for (BuildStep buildStep : steps) {
			reportBuildStep(currentPhase, buildStep, totalDuration);
			currentPhase = buildStep.phase;
		}
	}

	private void reportBuildStep(String currentPhase, BuildStep buildStep,
			long totalDuration) {
		String phase = buildStep.phase;
		long phaseDuration = totalDurationOfPhase(phase);
		long percentageOfPhase = (long) buildStep.duration().percentageOf(
				phaseDuration);
		if (phase != null && !phase.equals(currentPhase)) {
			reportPhaseStatistics(phase, totalDuration, phaseDuration);
		}
		reportGoalStatistics(buildStep, percentageOfPhase);
	}

	private void reportGoalStatistics(BuildStep buildStep, long percentage) {
		String goal = buildStep.artifactId + ":" + buildStep.goal;
		double seconds = buildStep.duration().inSeconds();
		logger.info(format("  %-48s %7.1fs %3s%% ", goal, seconds, percentage));
	}

	private void reportPhaseStatistics(String phase, long totalDuration,
			long totalDurationOfPhase) {
		Duration phaseDuration = new Duration(totalDurationOfPhase);
		long percentage = (long) phaseDuration.percentageOf(totalDuration);
		double seconds = phaseDuration.inSeconds();
		logger.info(format("%-50s %7.1fs %3s%%]", "[" + phase, seconds,
				percentage));
	}

	public long totalDurationOfPhase(String phase) {
		long total = 0;
		for (BuildStep e : steps) {
			if (phase != null && phase.equals(e.phase)) {
				total += e.duration().inMillis();
			}
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