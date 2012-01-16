package com.lassekoskela.maven.buildevents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.lassekoskela.maven.logging.ChainedLogger;

public class BuildEventLogReport_MultimoduleProjectTest extends
		AbstractBuildEventLogReportTest {
	@Before
	public void setUp() throws Exception {
		List<BuildStep> steps = new ArrayList<BuildStep>();
		steps.add(step("project-1", "phase-A", "goal-1A1", 100));
		steps.add(step("project-1", "phase-B", "goal-1B1", 2000));
		steps.add(step("project-1", "phase-B", "goal-1B2", 300));
		steps.add(step("project-2", "phase-A", "goal-2A1", 20));
		steps.add(step("project-2", "phase-A", "goal-2A2", 300));
		steps.add(step("project-2", "phase-B", "goal-2B1", 10));
		report = new BuildEventLogReport(new ChainedLogger(logger), steps);
	}

	@Test
	public void calculatesTotalDurationForBuild() {
		assertEquals(2730, report.totalDuration());
	}

	@Test
	public void calculatesTotalDurationForPhase() {
		assertEquals(100, report.totalDurationOfPhase("project-1", "phase-A"));
		assertEquals(2300, report.totalDurationOfPhase("project-1", "phase-B"));
		assertEquals(320, report.totalDurationOfPhase("project-2", "phase-A"));
		assertEquals(10, report.totalDurationOfPhase("project-2", "phase-B"));
	}

	@Test
	public void calculatesTotalDurationForProject() {
		assertEquals(2400, report.totalDurationOfProject("project-1"));
		assertEquals(330, report.totalDurationOfProject("project-2"));
	}

	@Test
	public void reportsDurationsByProject() throws Exception {
		report.report();
		assertThat(
				logger.output(),
				containsInOrder("BUILD STEP DURATIONS",
						projectStatsLine("project-1", 2.4, 87),
						projectStatsLine("project-2", 0.33, 12)));
	}

	@Test
	public void reportsDurationsByPhase() throws Exception {
		report.report();
		assertThat(
				logger.output(),
				containsInOrder("BUILD STEP DURATIONS",
						projectStatsLine("project-1", 2.4, 87),
						phaseStatsLine("phase-A", 0.1, 4),
						phaseStatsLine("phase-B", 2.3, 95),
						projectStatsLine("project-2", 0.33, 12),
						phaseStatsLine("phase-A", 0.3, 96),
						phaseStatsLine("phase-B", 0.01, 3)));
	}

	@Test
	public void reportsDurationsByGoal() throws Exception {
		report.report();
		assertThat(
				logger.output(),
				containsInOrder("BUILD STEP DURATIONS",
						projectStatsLine("project-1", 2.4, 87),
						phaseStatsLine("phase-A", 0.1, 4),
						goalStatsLine("goal-1A1", 0.1, 100),
						phaseStatsLine("phase-B", 2.3, 95),
						goalStatsLine("goal-1B1", 2.0, 86),
						goalStatsLine("goal-1B2", 0.3, 13),
						projectStatsLine("project-2", 0.33, 12),
						phaseStatsLine("phase-A", 0.3, 96),
						goalStatsLine("goal-2A1", 0.02, 6),
						goalStatsLine("goal-2A2", 0.3, 93),
						phaseStatsLine("phase-B", 0.01, 3),
						goalStatsLine("goal-2B1", 0.01, 100)));
	}
}
