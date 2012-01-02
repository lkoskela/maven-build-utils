package com.lassekoskela.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import com.lassekoskela.time.Clock;

public class ClockTest {
	@After
	public void tearDown() throws Exception {
		Clock.reset();
	}

	@Test
	public void ticks() throws Exception {
		assertTrue(clockIsTicking());
	}

	@Test
	public void canBeStoppedForTestingPurposes() throws Exception {
		Clock.freeze();
		assertFalse(clockIsTicking());
		long now = Clock.now();
		Clock.advance(200);
		assertEquals(now + 200, Clock.now());
	}

	@Test
	public void canBeRestarted() throws Exception {
		Clock.freeze();
		Clock.reset();
		assertTrue(clockIsTicking());
	}

	private boolean clockIsTicking() throws InterruptedException {
		long before = Clock.now();
		Thread.sleep(100);
		return Clock.now() > before;
	}
}
