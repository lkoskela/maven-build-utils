package com.lassekoskela.maven.bean;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.google.common.collect.Iterables;

public class TimelineTest {

	@Test
	public void testAddProject() {
		Timeline timeline = new Timeline(asList(new Project("prj", new ArrayList<Phase>())));
		assertEquals(Iterables.size(timeline.getProjects()), 1);
	}
}
