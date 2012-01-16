package com.lassekoskela.maven.buildevents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class BuildEventLog_SimpleProjectTest extends AbstractBuildEventLogTest {

	@Before
	public void setUp() throws Exception {
		simulator.goal("phase-A", "goal-A1", 100);
		simulator.goal("phase-B", "goal-B1", 2000);
		simulator.goal("phase-B", "goal-B2", 300);
		simulator.goal("phase-C", "goal-C1", 20);
		simulator.goal("phase-C", "goal-C2", 300);
		simulator.goal("phase-C", "goal-C3", 10);
	}

	@Test
	public void asksForTotalDurationFromTheReportObject() {
		assertEquals(2730, log.totalDuration());
		assertEquals(2730, log.totalDurationOfProject(simulator.project));
	}

	@Test
	public void asksForTotalDurationForPhaseFromTheReportObject() {
		assertEquals(100, log.totalDurationOfPhase("phase-A"));
		assertEquals(2300, log.totalDurationOfPhase("phase-B"));
		assertEquals(330, log.totalDurationOfPhase("phase-C"));
	}

	@Test
	public void producesAReportOnCue() throws Exception {
		log.report();
		assertThat(getReportedSteps(), hasBuildStep("phase-A", "goal-A1"));
		assertThat(getReportedSteps(), hasBuildStep("phase-B", "goal-B1"));
		assertThat(getReportedSteps(), hasBuildStep("phase-B", "goal-B2"));
		assertThat(getReportedSteps(), hasBuildStep("phase-C", "goal-C1"));
		assertThat(getReportedSteps(), hasBuildStep("phase-C", "goal-C2"));
		assertThat(getReportedSteps(), hasBuildStep("phase-C", "goal-C3"));
	}
}
