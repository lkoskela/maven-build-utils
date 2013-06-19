package com.lassekoskela.maven.bean;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;


public class ProjectTest {

	private Project project;
	
	@Before
	public void setup() {
		project = new Project("prj", Sets.<Phase> newHashSet());
	}
	
	@Test
	public void testAddPhase() {
		project.addPhase(new Phase("phase", Sets.<Goal> newHashSet()));
		
		assertEquals(project.getPhases().size(), 1);
	}
}
