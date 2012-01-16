package com.lassekoskela.maven.buildevents;

import static org.apache.maven.execution.ExecutionEvent.Type.MojoFailed;
import static org.apache.maven.execution.ExecutionEvent.Type.MojoStarted;
import static org.apache.maven.execution.ExecutionEvent.Type.MojoSucceeded;
import static org.apache.maven.execution.ExecutionEvent.Type.ProjectSucceeded;
import static org.apache.maven.execution.ExecutionEvent.Type.ProjectStarted;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionEvent.Type;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

public class BuildEventListenerTest {
	private static final String ARTIFACT_ID = "artifact";
	private static final String GROUP_ID = "group";
	private static final String PHASE = "phase";
	private BuildEventLog log;
	private BuildEventListener listener;
	private String currentProject;

	@Before
	public void setUp() throws Exception {
		log = mock(BuildEventLog.class);
		listener = new BuildEventListener(log);
		currentProject = "project";
	}
	
	@Test
	public void reportsProjectSwitches() throws Exception {
		currentProject = "project1";
		listener.projectStarted(event("", ProjectStarted));
		listener.mojoStarted(event("goal1", MojoStarted));
		listener.mojoSucceeded(event("goal1", MojoSucceeded));
		listener.projectSucceeded(event("", ProjectSucceeded));
		currentProject = "project2";
		listener.projectStarted(event("", ProjectStarted));
		listener.mojoStarted(event("goal2", MojoStarted));
		listener.mojoSucceeded(event("goal2", MojoSucceeded));
		listener.projectSucceeded(event("", ProjectSucceeded));
		verify(log).start("project1", PHASE, GROUP_ID, ARTIFACT_ID, "goal1");
		verify(log).end("project1", PHASE, GROUP_ID, ARTIFACT_ID, "goal1");
		verify(log).start("project2", PHASE, GROUP_ID, ARTIFACT_ID, "goal2");
		verify(log).end("project2", PHASE, GROUP_ID, ARTIFACT_ID, "goal2");
	}

	@Test
	public void picksUpMojoStartingEvents() throws Exception {
		listener.mojoStarted(event("goal", MojoStarted));
		verify(log).start(currentProject, PHASE, GROUP_ID, ARTIFACT_ID, "goal");
	}

	@Test
	public void picksUpMojoEndingEvents() throws Exception {
		listener.mojoSucceeded(event("winning-goal", MojoSucceeded));
		listener.mojoFailed(event("failing-goal", MojoFailed));
		verify(log).end(currentProject, PHASE, GROUP_ID, ARTIFACT_ID, "winning-goal");
		verify(log).end(currentProject, PHASE, GROUP_ID, ARTIFACT_ID, "failing-goal");
	}

	private ExecutionEvent event(String goal, Type eventType) {
		return event(PHASE, GROUP_ID, ARTIFACT_ID, goal, eventType);
	}

	private ExecutionEvent event(String phase, String group, String artifact,
			String goal, Type eventType) {
		MojoExecution execution = mock(MojoExecution.class);
		when(execution.getLifecyclePhase()).thenReturn(phase);
		when(execution.getGroupId()).thenReturn(group);
		when(execution.getArtifactId()).thenReturn(artifact);
		when(execution.getGoal()).thenReturn(goal);
		ExecutionEvent event = mock(ExecutionEvent.class);
		when(event.getType()).thenReturn(eventType);
		when(event.getMojoExecution()).thenReturn(execution);
		MavenProject project = mock(MavenProject.class);
		when(project.getGroupId()).thenReturn(GROUP_ID);
		when(project.getArtifactId()).thenReturn(currentProject);
		when(event.getProject()).thenReturn(project);
		return event;
	}
}
