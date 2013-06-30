package com.lassekoskela.maven.timeline;

import static com.lassekoskela.maven.timeline.ObjectBuilder.goal;
import static com.lassekoskela.maven.timeline.ObjectBuilder.phase;
import static com.lassekoskela.maven.timeline.ObjectBuilder.project;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
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

import com.lassekoskela.maven.bean.Phase;
import com.lassekoskela.maven.bean.Project;
import com.lassekoskela.time.DateProvider;

public class BuildTimelineListenerTest {

	private BuildTimelineListener listener;
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

		listener = new BuildTimelineListener(new ConsoleLogger(), dateProvider);
	}

	@Test
	public void calculatesRelativeNowFromBuildStartTime() {
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(1500l));

		assertThat(listener.relativeNowFromBuildStartTime(event), is(500l));
	}

	@Test
	public void relativeNowAtBuildStartTimeIsZero() {
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(1000l));

		assertThat(listener.relativeNowFromBuildStartTime(event), is(0l));
	}

	@Test(expected = TimelineExportException.class)
	public void relativeNowFromBuildStartTimeBeforeBuildStartTimeRaisesAnException() {
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(900l));
		listener.relativeNowFromBuildStartTime(event);
	}

	@Test
	public void projectOfMojoIgnoresDuplicateProjectNames() {
		listener.projectOfMojo("project1");
		listener.projectOfMojo("project1");

		assertThat(listener.nameToProjectMapping.size(), is(1));
		assertThat(listener.nameToProjectMapping, hasEntry("project1", project("project1")));
	}

	@Test
	public void projectOfMojoKeepsMappingBetweenProjectNamesAndObjects() {
		listener.projectOfMojo("project1");
		assertThat(listener.nameToProjectMapping, hasEntry("project1", project("project1")));
		listener.projectOfMojo("project2");
		assertThat(listener.nameToProjectMapping,
				allOf(hasEntry("project1", project("project1")), hasEntry("project2", project("project2"))));
	}

	@Test
	public void phaseOfMojoRecordsPhaseForTheProjectInQuestion() {
		Project project = project("project1");
		listener.phaseOfMojo("phase1", project);
		listener.phaseOfMojo("phase2", project);
		assertThat(project.getPhases(), contains(phase("phase1"), phase("phase2")));
	}

	@Test
	public void phaseOfMojoRecordsEachPhaseOnlyOnceForTheProjectInQuestion() {
		Project project = project("project1");
		listener.phaseOfMojo("phase1", project);
		listener.phaseOfMojo("phase1", project);

		assertThat(project.getPhases(), contains(phase("phase1")));
		assertThat(project.getPhases(), hasSize(1));
	}

	@Test
	public void mojoStartedRecordsTheSameGoalOnlyOnce() {
		Phase phase = phase("phase1");
		Project project = project("project1", phase);
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(1500l));
		when(execution.getGoal()).thenReturn("goal1");
		phase.addGoal(goal("goal1", 0, 500));

		listener.mojoStarted(event, project, phase);

		assertThat(phase.getGoals(), hasSize(1));
		assertThat(phase.getGoals(), contains(goal("goal1", 0, 500)));
	}

	@Test
	public void mojoStartedRecordsGoalsForTheProjectAndPhaseInQuestion() {
		Phase phase = phase("phase1");
		Project project = project("project1", phase);
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(1500l));
		when(execution.getGoal()).thenReturn("goal1");
		phase.addGoal(goal("goal0", 0, 500));

		listener.mojoStarted(event, project, phase);

		assertThat(phase.getGoals(), contains(goal("goal0", 0, 500), goal("goal1", 0, 500)));
	}

	@Test
	public void mojoEndedRecordsTheDurationOfTheGoal() {
		when(session.getStartTime()).thenReturn(new Date(1000l));
		when(dateProvider.now()).thenReturn(new Date(1500l), new Date(2000l));
		when(execution.getGoal()).thenReturn("goal1");

		Project project = listener.projectOfMojo("project1");
		Phase phase = listener.phaseOfMojo("phase1", project);
		listener.mojoStarted(event, project, phase);
		listener.mojoEnded(event);

		assertThat(phase.getGoals(), contains(goal("goal1", 500, 500)));
	}
}
