/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package com.lassekoskela.maven.timeline;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.lassekoskela.maven.bean.Goal;
import com.lassekoskela.maven.bean.Phase;
import com.lassekoskela.maven.bean.Project;
import com.lassekoskela.time.DateProvider;
import com.lassekoskela.time.Duration;


public class BuildTimelineListenerTest {

	private BuildTimelineListener testee;
	private DateProvider dateProvider;
	private ExecutionEvent event;
	private MavenSession session;
	private MojoExecution execution;

	@Before
	public void setUp() {
		dateProvider = mock(DateProvider.class);
		event = mock(ExecutionEvent.class);
		session = mock(MavenSession.class);
		execution = mock(MojoExecution.class);
		when(event.getSession()).thenReturn(session);
		when(event.getMojoExecution()).thenReturn(execution);
		
		testee = new BuildTimelineListener(new ConsoleLogger(), dateProvider);
	}
	
	@Test
	public void testRelativeNowFromBuildStartTime() {
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(1500l));
		
		assertThat(testee.relativeNowFromBuildStartTime(event), is(500l));
	}
	
	@Test
	public void testRelativeNowFromBuildStartTimeWhenSameDate() {
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(1000l));
		
		assertThat(testee.relativeNowFromBuildStartTime(event), is(0l));
	}
	
	@Test(expected=TimelineExportException.class)
	public void testRelativeNowFromBuildStartTimeWhenLowerDate() {
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(900l));
		testee.relativeNowFromBuildStartTime(event);
	}

	@Test
	public void testProjectOfMojoWhenEmpty() {
		testee.projectOfMojo("project1");
		
		assertThat(testee.nameToProjectMapping, 
				hasEntry("project1", new Project("project1", ImmutableSet.<Phase>of())));
	}

	@Test
	public void testProjectOfMojoWhenContainsSameEntry() {
		testee.projectOfMojo("project1");
		testee.projectOfMojo("project1");
		
		assertThat(testee.nameToProjectMapping.size(), is(1));
		assertThat(testee.nameToProjectMapping, 
				hasEntry("project1", new Project("project1", ImmutableSet.<Phase>of())));
	}

	@Test
	public void testProjectOfMojoWhenContainsAnotherEntry() {
		testee.projectOfMojo("project1");
		testee.projectOfMojo("project2");
		
		assertThat(testee.nameToProjectMapping, allOf(
				hasEntry("project1", new Project("project1", ImmutableSet.<Phase>of())),
				hasEntry("project2", new Project("project2", ImmutableSet.<Phase>of()))));
	}

	@Test(expected=NullPointerException.class)
	public void testPhaseOfMojoWhenNullProject() {
		Project project = null;
		testee.phaseOfMojo("phase1", project);
	}

	@Test
	public void testPhaseOfMojoWhenEmpty() {
		Project project = new Project("project1", Sets.<Phase>newHashSet());
		testee.phaseOfMojo("phase1", project);
		
		assertThat(project.getPhases(), 
				contains(new Phase("phase1", ImmutableSet.<Goal>of())));
	}

	@Test
	public void testPhaseOfMojoWhenContainsSameEntry() {
		Project project = new Project("project1", Sets.<Phase>newHashSet());
		testee.phaseOfMojo("phase1", project);
		testee.phaseOfMojo("phase1", project);

		assertThat(project.getPhases(), hasSize(1));
		assertThat(project.getPhases(), contains(new Phase("phase1", ImmutableSet.<Goal>of())));
	}

	@Test
	public void testPhaseOfMojoWhenContainsAnotherEntry() {
		Project project = new Project("project1", Sets.<Phase>newHashSet());
		testee.phaseOfMojo("phase1", project);
		testee.phaseOfMojo("phase2", project);
		
		assertThat(project.getPhases(), contains(
				new Phase("phase1", ImmutableSet.<Goal>of()),
				new Phase("phase2", ImmutableSet.<Goal>of())));
	}

	@Test
	public void testMojoStartedWhenEmpty() {
		Phase phase = new Phase("phase1", Sets.<Goal>newHashSet());
		Project project = new Project("project1", ImmutableSet.of(phase));
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(1500l));
		when(execution.getGoal()).thenReturn("goal1");
		
		testee.mojoStarted(event, project, phase);
		
		assertThat(phase.getGoals(), contains(
				new Goal("goal1", new Duration(0), 500, ImmutableSet.<String>of())));
	}

	@Test
	public void testMojoStartedWhenSameEntry() {
		Phase phase = new Phase("phase1", Sets.<Goal>newHashSet());
		Project project = new Project("project1", ImmutableSet.of(phase));
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(1500l));
		when(execution.getGoal()).thenReturn("goal1");
		phase.addGoal(new Goal("goal1", new Duration(0), 500, ImmutableSet.<String>of()));
		
		testee.mojoStarted(event, project, phase);

		assertThat(phase.getGoals(), hasSize(1));
		assertThat(phase.getGoals(), contains(new Goal("goal1", new Duration(0), 500, ImmutableSet.<String>of())));
	}

	@Test
	public void testMojoStartedWhenAnotherEntry() {
		Phase phase = new Phase("phase1", Sets.<Goal>newHashSet());
		Project project = new Project("project1", ImmutableSet.of(phase));
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(1500l));
		when(execution.getGoal()).thenReturn("goal1");
		phase.addGoal(new Goal("goal0", new Duration(0), 500, ImmutableSet.<String>of()));
		
		testee.mojoStarted(event, project, phase);

		assertThat(phase.getGoals(), contains(
				new Goal("goal0", new Duration(0), 500, ImmutableSet.<String>of()),
				new Goal("goal1", new Duration(0), 500, ImmutableSet.<String>of())));
	}

	@Test(expected=NullPointerException.class)
	public void testMojoEndedWhenGoalNotStarted() {
		when(execution.getGoal()).thenReturn("goal1");
		
		testee.mojoEnded(event);
	}

	@Test
	public void testMojoEndedWhenGoalStarted() {
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(1500l), new Date(2000l));
		when(execution.getGoal()).thenReturn("goal1");
		
		Project project = testee.projectOfMojo("project1");
		Phase phase = testee.phaseOfMojo("phase1", project);
		testee.mojoStarted(event, project, phase);
		
		testee.mojoEnded(event);
		
		assertThat(phase.getGoals(), contains(
				new Goal("goal1", new Duration(500), 500, ImmutableSet.<String>of())));
	}
}
