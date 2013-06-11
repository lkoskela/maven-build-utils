package com.lassekoskela.maven.bean;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;


public class TimelineTest {

	
	@Test
	public void testAddProject() {
		Timeline timeline = new Timeline(ImmutableSet.of(
				new Project("prj", Sets.<Phase> newHashSet())));
		
		assertEquals(Iterables.size(timeline.getProjects()), 1);
	}
}
