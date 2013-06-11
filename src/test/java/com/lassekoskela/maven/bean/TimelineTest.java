package com.lassekoskela.maven.bean;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.lassekoskela.time.Duration;


public class TimelineTest {

	
	@Test
	public void testAddProject() {
		Timeline timeline = new Timeline(ImmutableSet.of(
				new Project("prj", new Duration(2400), Sets.<Phase> newHashSet())));
		
		assertEquals(Iterables.size(timeline.getProjects()), 1);
	}
}
