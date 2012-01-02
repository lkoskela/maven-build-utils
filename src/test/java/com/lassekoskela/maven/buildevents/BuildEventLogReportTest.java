package com.lassekoskela.maven.buildevents;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lassekoskela.maven.buildevents.BuildEventLogReport;
import com.lassekoskela.maven.buildevents.BuildStep;
import com.lassekoskela.maven.logging.ChainedLogger;
import com.lassekoskela.time.Clock;

public class BuildEventLogReportTest {
	private Logger logger;
	private BuildEventLogReport report;
	private List<BuildStep> steps;

	@Before
	public void setUp() throws Exception {
		Clock.freeze();
		logger = mock(Logger.class);
		steps = new ArrayList<BuildStep>();
		steps.add(step("phase-A", "goal-A1", 100));
		steps.add(step("phase-B", "goal-B1", 2000));
		steps.add(step("phase-B", "goal-B2", 300));
		steps.add(step("phase-C", "goal-C1", 20));
		steps.add(step("phase-C", "goal-C2", 300));
		steps.add(step("phase-C", "goal-C3", 10));
		report = new BuildEventLogReport(new ChainedLogger(logger), steps);
	}

	@After
	public void tearDown() throws Exception {
		Clock.reset();
	}

	@Test
	public void calculatesTotalDurationForBuild() {
		assertEquals(2730, report.totalDuration());
	}

	@Test
	public void calculatesTotalDurationForPhase() {
		assertEquals(100, report.totalDurationOfPhase("phase-A"));
		assertEquals(2300, report.totalDurationOfPhase("phase-B"));
		assertEquals(330, report.totalDurationOfPhase("phase-C"));
	}

	@Test
	public void reportsDurationsByGoal() throws Exception {
		report.report();
		verify(logger).info(contains("BUILD STEP DURATIONS"));
		verify(logger).info(goalStatsLine("goal-A1", 0.1, 100));
		verify(logger).info(goalStatsLine("goal-B1", 2.0, 86));
		verify(logger).info(goalStatsLine("goal-B2", 0.3, 13));
		verify(logger).info(goalStatsLine("goal-C1", 0.02, 6));
		verify(logger).info(goalStatsLine("goal-C2", 0.3, 90));
		verify(logger).info(goalStatsLine("goal-C3", 0.01, 3));
	}

	@Test
	public void reportsDurationsByPhase() throws Exception {
		report.report();
		verify(logger).info(contains("BUILD STEP DURATIONS"));
		verify(logger).info(phaseStatsLine("phase-A", 0.1, 3));
		verify(logger).info(phaseStatsLine("phase-B", 2.3, 84));
		verify(logger).info(phaseStatsLine("phase-C", 0.3, 12));
	}

	private BuildStep step(String phase, String goal, long duration) {
		BuildStep step = new BuildStep(phase, "group", "artifact", goal);
		step.start();
		Clock.advance(duration);
		step.end();
		return step;
	}

	private String goalStatsLine(String goal, double durationInSeconds,
			double percentageOfPhase) {
		String duration = durationString(durationInSeconds);
		String percentage = percentageString(percentageOfPhase);
		return matchesTokensInSequence("artifact:" + goal, duration, percentage);
	}

	private String phaseStatsLine(String phase, double durationInSeconds,
			double percentageOfTotal) {
		String duration = durationString(durationInSeconds);
		String percentage = percentageString(percentageOfTotal);
		return matchesTokensInSequence("[", phase, duration, percentage, "]");
	}

	private String percentageString(double percentageOfPhase) {
		return format("%.0f", percentageOfPhase) + "%";
	}

	private String durationString(double durationInSeconds) {
		return format("%.1f", durationInSeconds) + "s";
	}

	private String matchesTokensInSequence(String... tokens) {
		StringBuilder regex = new StringBuilder();
		regex.append("^");
		for (String token : tokens) {
			token = escapeForRegex(token);
			regex.append("(.*)(").append(token).append(")");
		}
		regex.append("(.*)$");
		return matches(regex.toString());
	}

	private String escapeForRegex(String token) {
		for (char c : "[].()".toCharArray()) {
			token = token.replace("" + c, "\\" + c);
		}
		return token;
	}
}
