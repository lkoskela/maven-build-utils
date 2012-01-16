package com.lassekoskela.maven.buildevents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.lassekoskela.maven.logging.ChainedLogger;

public class BuildEventLogReport_SimpleProjectTest extends
		AbstractBuildEventLogReportTest {
	@Before
	public void setUp() throws Exception {
		List<BuildStep> steps = new ArrayList<BuildStep>();
		steps.add(step("phase-A", "goal-A1", 100));
		steps.add(step("phase-B", "goal-B1", 2000));
		steps.add(step("phase-B", "goal-B2", 300));
		steps.add(step("phase-C", "goal-C1", 20));
		steps.add(step("phase-C", "goal-C2", 300));
		steps.add(step("phase-C", "goal-C3", 10));
		report = new BuildEventLogReport(new ChainedLogger(logger), steps);
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
	public void calculatesTotalDurationForProject() {
		assertEquals(2730, report.totalDurationOfProject("project"));
	}

	@Test
	public void reportsDurationsByGoal() throws Exception {
		report.report();
		assertThat(
				logger.output(),
				containsInOrder("BUILD STEP DURATIONS",
						goalStatsLine("goal-A1", 0.1, 100),
						goalStatsLine("goal-B1", 2.0, 86),
						goalStatsLine("goal-B2", 0.3, 13),
						goalStatsLine("goal-C1", 0.02, 6),
						goalStatsLine("goal-C2", 0.3, 90),
						goalStatsLine("goal-C3", 0.01, 3)));
	}

	@Test
	public void reportsDurationsByPhase() throws Exception {
		report.report();
		assertThat(
				logger.output(),
				containsInOrder("BUILD STEP DURATIONS",
						phaseStatsLine("phase-A", 0.1, 3),
						phaseStatsLine("phase-B", 2.3, 84),
						phaseStatsLine("phase-C", 0.3, 12)));
	}
}
