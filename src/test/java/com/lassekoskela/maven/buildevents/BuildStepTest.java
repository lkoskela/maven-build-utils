package com.lassekoskela.maven.buildevents;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lassekoskela.maven.buildevents.BuildStep;
import com.lassekoskela.time.Clock;
import com.lassekoskela.time.Duration;

public class BuildStepTest {
	private BuildStep step;

	@Before
	public void setUp() throws Exception {
		Clock.freeze();
		step = new BuildStep("project", "phase", "group", "artifact", "goal");
	}

	@After
	public void tearDown() throws Exception {
		Clock.reset();
	}

	@Test
	public void startingAndEndingAStepRecordsDuration() {
		step.start();
		Clock.advance(123);
		step.end();
		assertEquals(new Duration(123), step.duration());
	}
}
