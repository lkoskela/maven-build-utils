package com.lassekoskela.maven.bean;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.lassekoskela.time.Duration;

public class GoalTest {

	@Test
	public void testGoalNoDependencies() {
		Goal goal = new Goal("goal", new Duration(2000), 100, new ArrayList<String>());
		assertTrue(goal.getDependencies().isEmpty());
		assertTrue(goal.serializeDependencies().isEmpty());
	}

	@Test
	public void testGoalThreeDependencies() {
		Goal goal = new Goal("goal", new Duration(2000), 100, asList("dep1", "dep2", "dep3"));
		assertEquals(asList("dep1", "dep2", "dep3"), goal.getDependencies());
		assertEquals("dep1 dep2 dep3", goal.serializeDependencies());
	}

	@Test
	public void testGoalGetCompletedTimeWhenZero() {
		Goal goal = new Goal("goal", new Duration(0), 0, new ArrayList<String>());
		assertEquals(goal.getCompletedTimeInMs(), 0);
	}

	@Test
	public void testGoalGetCompletedTime() {
		Goal goal = new Goal("goal", new Duration(1200), 100, new ArrayList<String>());
		assertEquals(goal.getCompletedTimeInMs(), 1300);
	}
}