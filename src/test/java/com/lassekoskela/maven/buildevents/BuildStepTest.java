package com.lassekoskela.maven.buildevents;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lassekoskela.maven.buildevents.BuildStep;
import com.lassekoskela.time.Clock;
import com.lassekoskela.time.Duration;

public class BuildStepTest {
	@Before
	public void setUp() throws Exception {
		Clock.freeze();
	}

	@After
	public void tearDown() throws Exception {
		Clock.reset();
	}

	@Test
	public void startingAndEndingAStepRecordsDuration() {
		BuildStep step = new BuildStep("phase", "group", "artifact", "goal");
		step.start();
		Clock.advance(123);
		step.end();
		assertEquals(new Duration(123), step.duration());
	}
}
