package com.lassekoskela.time;

import static com.lassekoskela.time.DurationBuilder.newDuration;
import static java.lang.Math.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DurationTest {
	@Test
	public void knowsItsDurationInMilliseconds() {
		long millis = (long) (random() * 10000);
		assertEquals(millis, new Duration(millis).inMillis());
	}

	@Test
	public void knowsItsDurationInSeconds() throws Exception {
		assertEquals(0.1, new Duration(100).inSeconds(), 0.01);
		assertEquals(1.0, new Duration(1000).inSeconds(), 0.01);
		assertEquals(1.5, new Duration(1500).inSeconds(), 0.01);
	}

	@Test
	public void printsNicely() throws Exception {
		Duration d = newDuration().hours(1).minutes(23).seconds(45).millis(678)
				.build();
		assertEquals("1h 23m 45s 678ms", d.toString());
	}

	@Test
	public void printsNicelySkippingZerosInTheMiddle() throws Exception {
		Duration d = newDuration().hours(1).millis(678).build();
		assertEquals("1h 678ms", d.toString());
	}

	@Test
	public void calculatesPercentageOfAnotherDuration() throws Exception {
		Duration d = new Duration(300);
		assertEquals(30.0, d.percentageOf(1000), 0.001);
		assertEquals(25.0, d.percentageOf(new Duration(1200)), 0.001);
	}

	@Test
	public void equals() throws Exception {
		Duration d = new Duration(500);
		assertTrue(d.equals(d));
		assertTrue(d.equals(new Duration(500)));
		assertFalse(d.equals(new Duration(300)));
		assertFalse(d.equals(new Duration(500) {
			// subclass
		}));
	}
}
