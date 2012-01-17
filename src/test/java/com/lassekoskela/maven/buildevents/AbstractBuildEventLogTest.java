package com.lassekoskela.maven.buildevents;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;

import com.lassekoskela.time.Clock;

public abstract class AbstractBuildEventLogTest {
	private List<BuildStep> reportSteps = new ArrayList<BuildStep>();
	protected BuildEventLog log;
	protected boolean reportWasCreated;
	protected EventSimulator simulator;

	private void recordReportedSteps(List<BuildStep> steps) {
		this.reportSteps = steps;
	}

	public List<BuildStep> getReportedSteps() {
		return unmodifiableList(reportSteps);
	}

	@Before
	public final void _createFixtureObjects() throws Exception {
		Clock.freeze();
		reportWasCreated = false;
		log = new BuildEventLog() {
			@Override
			protected BuildEventLogReport createReport(
					final List<BuildStep> steps) {
				return new BuildEventLogReport(steps) {
					@Override
					public void report(java.io.PrintWriter writer) {
						recordReportedSteps(steps);
						reportWasCreated = true;
					}
				};
			}
		};
		simulator = new EventSimulator(log);
	}

	@After
	public final void _tearDownFixtureObjects() throws Exception {
		Clock.reset();
	}

	protected Matcher<Iterable<BuildStep>> hasBuildStep(String phase,
			String goal) {
		return hasBuildStep(simulator.project, phase, goal);
	}

	protected Matcher<Iterable<BuildStep>> hasBuildStep(String project,
			String phase, String goal) {
		BuildStep step = new BuildStep(project, phase, simulator.groupId,
				simulator.artifactId, goal);
		return Matchers.hasItem(step);
	}
}
