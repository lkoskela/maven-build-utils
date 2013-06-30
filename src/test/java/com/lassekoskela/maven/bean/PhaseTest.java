package com.lassekoskela.maven.bean;

import static com.lassekoskela.maven.timeline.ObjectBuilder.goal;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class PhaseTest {

	private Phase phase;

	@Before
	public void setup() {
		phase = new Phase("prj", new ArrayList<Goal>());
	}

	@Test
	public void testAddGoal() {
		phase.addGoal(goal("goal", 2000, 1000));
		assertEquals(phase.getGoals().size(), 1);
	}
}
