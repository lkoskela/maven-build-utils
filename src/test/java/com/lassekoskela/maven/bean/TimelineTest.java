package com.lassekoskela.maven.bean;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.lassekoskela.time.Duration;


public class TimelineTest {

	private Timeline timeline;
	
	@Before
	public void setup() {
		timeline = new Timeline(Sets.<Project> newHashSet());
	}
	
	@Test
	public void testAddProject() {
		timeline.addProject(new Project("prj", new Duration(2400), Sets.<Phase> newHashSet()));
		
		assertEquals(timeline.getProjects().size(), 1);
	}
}
