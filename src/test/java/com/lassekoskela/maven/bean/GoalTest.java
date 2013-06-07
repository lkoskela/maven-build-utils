package com.lassekoskela.maven.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.lassekoskela.time.Duration;


public class GoalTest {

	@Test
	public void testGoalNoDependencies() {
		Goal goal = new Goal("goal", new Duration(2000), 100, Sets.<String> newHashSet());
		
		assertTrue(goal.getDependencies().isEmpty());
		assertTrue(goal.serializeDependencies().isEmpty());
	}
	
	@Test
	public void testGoalThreeDependencies() {
		Goal goal = new Goal("goal", new Duration(2000), 100, Sets.newHashSet("dep1", "dep2", "dep3"));
		
		assertEquals(goal.getDependencies(), Sets.newHashSet("dep1", "dep2", "dep3"));
		assertEquals(goal.serializeDependencies(), "dep1 dep2 dep3");
	}
	
	@Test
	public void testGoalGetCompletedTimeWhenZero() {
		Goal goal = new Goal("goal", new Duration(0), 0, Sets.<String>newHashSet());
		
		assertEquals(goal.getCompletedTimeInMs(), 0);
	}
	
	@Test
	public void testGoalGetCompletedTime() {
		Goal goal = new Goal("goal", new Duration(1200), 100, Sets.<String>newHashSet());
		
		assertEquals(goal.getCompletedTimeInMs(), 1300);
	}
}
