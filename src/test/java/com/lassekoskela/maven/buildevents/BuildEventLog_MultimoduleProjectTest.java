package com.lassekoskela.maven.buildevents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class BuildEventLog_MultimoduleProjectTest extends
		AbstractBuildEventLogTest {
	@Before
	public void setUp() throws Exception {
		simulator.goal("project-1", "phase-A", "goal-1A1", 100);
		simulator.goal("project-1", "phase-A", "goal-1A2", 100);
		simulator.goal("project-1", "phase-B", "goal-1B1", 100);
		simulator.goal("project-2", "phase-A", "goal-2A1", 100);
		simulator.goal("project-2", "phase-B", "goal-2B1", 100);
	}

	@Test
	public void asksForTotalDurationFromTheReportObject() {
		assertEquals(500, log.totalDuration());
	}
	
	@Test
	public void asksForTotalDurationForProjectFromTheReportObject() {
		assertEquals(300, log.totalDurationOfProject("project-1"));
		assertEquals(200, log.totalDurationOfProject("project-2"));
	}
	
	@Test
	public void asksForTotalDurationForProjectPhaseFromTheReportObject() {
		assertEquals(200, log.totalDurationOfPhase("project-1", "phase-A"));
		assertEquals(100, log.totalDurationOfPhase("project-2", "phase-B"));
	}

	@Test
	public void producesAReportOnCue() throws Exception {
		log.report();
		assertThat(getReportedSteps(), hasBuildStep("project-1", "phase-A", "goal-1A1"));
		assertThat(getReportedSteps(), hasBuildStep("project-1", "phase-A", "goal-1A2"));
		assertThat(getReportedSteps(), hasBuildStep("project-1", "phase-B", "goal-1B1"));
		assertThat(getReportedSteps(), hasBuildStep("project-2", "phase-A", "goal-2A1"));
		assertThat(getReportedSteps(), hasBuildStep("project-2", "phase-B", "goal-2B1"));
	}
}
