package com.lassekoskela.maven.buildevents;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.codehaus.plexus.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lassekoskela.maven.buildevents.BuildEventLog;
import com.lassekoskela.maven.buildevents.BuildEventLogReport;
import com.lassekoskela.maven.buildevents.BuildStep;
import com.lassekoskela.time.Clock;

public class BuildEventLogTest {
	private Logger logger;
	private BuildEventLog log;
	protected List<BuildStep> reportSteps;
	protected boolean reportWasCreated;

	@Before
	public void setUp() throws Exception {
		Clock.freeze();
		logger = mock(Logger.class);
		reportWasCreated = false;
		log = new BuildEventLog(logger) {
			@Override
			protected BuildEventLogReport createReport(Logger logger,
					List<BuildStep> steps) {
				return new BuildEventLogReport(logger, steps) {
					@Override
					public void report() {
						reportWasCreated = true;
					}
				};
			}
		};
		addEvent("phase-A", "goal-A1", 100);
		addEvent("phase-B", "goal-B1", 2000);
		addEvent("phase-B", "goal-B2", 300);
		addEvent("phase-C", "goal-C1", 20);
		addEvent("phase-C", "goal-C2", 300);
		addEvent("phase-C", "goal-C3", 10);
	}

	@After
	public void tearDown() throws Exception {
		Clock.reset();
	}

	@Test
	public void asksForTotalDurationFromTheReportObject() {
		assertEquals(2730, log.totalDuration());
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
	}

	private void addEvent(String phase, String goal, long durationInMillis) {
		log.start(phase, "group", "artifact", goal);
		Clock.advance(durationInMillis);
		log.end(phase, "group", "artifact", goal);
	}
}
