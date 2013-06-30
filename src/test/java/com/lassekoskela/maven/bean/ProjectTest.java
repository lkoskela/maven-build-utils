package com.lassekoskela.maven.bean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class ProjectTest {

	private Project project;

	@Before
	public void setup() {
		project = new Project("prj", new ArrayList<Phase>());
	}

	@Test
	public void testAddPhase() {
		project.addPhase(new Phase("phase", new ArrayList<Goal>()));
		assertEquals(project.getPhases().size(), 1);
	}
}
