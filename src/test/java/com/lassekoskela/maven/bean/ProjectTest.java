package com.lassekoskela.maven.bean;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.lassekoskela.time.Duration;


public class ProjectTest {

	private Project project;
	
	@Before
	public void setup() {
		project = new Project("prj", new Duration(2400), Sets.<Phase> newHashSet());
	}
	
	@Test
	public void testAddPhase() {
		project.addPhase(new Phase("phase", new Duration(1200), Sets.<Goal> newHashSet()));
		
		assertEquals(project.getPhases().size(), 1);
	}
}
