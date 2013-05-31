package com.lassekoskela.maven.bean;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.lassekoskela.time.Duration;


public class PhaseTest {

	private Phase phase;
	
	@Before
	public void setup() {
		phase = new Phase("prj", new Duration(2400), Sets.<Goal> newHashSet());
	}
	
	@Test
	public void testAddGoal() {
		phase.addGoal(new Goal("goal", new Duration(1200)));
		
		assertEquals(phase.getGoals().size(), 1);
	}
}
