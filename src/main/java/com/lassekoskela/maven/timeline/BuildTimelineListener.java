package com.lassekoskela.maven.timeline;

import java.io.FileNotFoundException;
import java.util.Map;

import org.apache.maven.execution.AbstractExecutionListener;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lassekoskela.maven.bean.Goal;
import com.lassekoskela.maven.bean.Phase;
import com.lassekoskela.maven.bean.Project;
import com.lassekoskela.maven.bean.Timeline;
import com.lassekoskela.time.DateProvider;
import com.lassekoskela.time.DateProviderImpl;
import com.lassekoskela.time.Duration;

public class BuildTimelineListener extends AbstractExecutionListener {

	private final Logger logger;
	private final DateProvider dateProvider;
	@VisibleForTesting final Map<String, Project> nameToProjectMapping;
	@VisibleForTesting final Map<String, Goal> nameToGoalMapping;
	
	public BuildTimelineListener(Logger logger) {
		this(logger, new DateProviderImpl());
	}
	
	public BuildTimelineListener(Logger logger, DateProvider dateProvider) {
		this.logger = logger;
		this.dateProvider = dateProvider;
		this.nameToProjectMapping = Maps.newHashMap();
		this.nameToGoalMapping = Maps.newHashMap();
	}

	@Override
	public void sessionEnded(ExecutionEvent event) {
		try {
			new Exporter(logger).export(new Timeline(nameToProjectMapping.values()));
		} catch (TimelineExportException e) {
			Throwables.propagate(e);
		} catch (FileNotFoundException e) {
			Throwables.propagate(e);
		}
	}

	@Override
	public void mojoStarted(ExecutionEvent event) {
		Project project = projectOfMojo(event.getProject().getArtifactId());
		Phase phase = phaseOfMojo(event.getMojoExecution().getLifecyclePhase(), project);
		mojoStarted(event, project, phase);
	}
	
	@Override
	public void mojoSucceeded(ExecutionEvent event) {
		mojoEnded(event);
	}

	@Override
	public void mojoFailed(ExecutionEvent event) {
		mojoEnded(event);
	}

	@VisibleForTesting void mojoEnded(ExecutionEvent event) {
		Goal endedGoal = nameToGoalMapping.get(event.getMojoExecution().getGoal());
		endedGoal.setDuration(new Duration(relativeNowFromBuildStartTime(event) - endedGoal.getStartTimeInMs()));
		logger.info(String.format("[Timeline] The Goal[%s:%s:%s] started at [%dms] has finished at [%dms], elapsed[%dms]",
				event.getMojoExecution().getArtifactId(),
				event.getMojoExecution().getLifecyclePhase(), endedGoal.getItemId(), endedGoal.getStartTimeInMs(),
				endedGoal.getCompletedTimeInMs(), endedGoal.getDuration().inMillis()));
	}

	@VisibleForTesting void mojoStarted(ExecutionEvent event, Project project, Phase phase) {
		String goalName = event.getMojoExecution().getGoal();
		if (!phase.getGoal(goalName).isPresent()) {
			goalStarted(event, project, phase, goalName);
		} else {
			logger.info(String.format("[Timeline] A Goal seems to be started twice in (%s:%s) %s",
					project.getItemId(), phase.getItemId(), goalName));
		}
	}

	@VisibleForTesting void goalStarted(ExecutionEvent event, Project project, Phase phase, String goalName) {
		long buildStartTimeInMs = relativeNowFromBuildStartTime(event);
		Goal newGoal = new Goal(goalName, new Duration(0), buildStartTimeInMs, Sets.<String>newHashSet());
		phase.addGoal(newGoal);
		nameToGoalMapping.put(goalName, newGoal);

		logger.info(String.format("[Timeline] Add a new Goal[%s:%s:%s] started at [%dms]",
				project.getItemId(), phase.getItemId(), goalName, buildStartTimeInMs));
	}

	@VisibleForTesting Phase phaseOfMojo(String phaseName, Project project) {
		Optional<Phase> phase = project.getPhase(phaseName);
		if (!phase.isPresent()) {
			logger.info(String.format("[Timeline] Add a new Phase[%s:%s]", project.getItemId(), phaseName));
			return project.addPhase(new Phase(phaseName, Sets.<Goal>newHashSet()));
		}
		return phase.get();
	}

	@VisibleForTesting Project projectOfMojo(String projectName) {
		if (!nameToProjectMapping.containsKey(projectName)) {
			logger.info("[Timeline] Add a new Project[" + projectName + "]");
			nameToProjectMapping.put(projectName, new Project(projectName, Sets.<Phase>newHashSet()));
		}
		return nameToProjectMapping.get(projectName);
	}

	@VisibleForTesting long relativeNowFromBuildStartTime(ExecutionEvent event) {
		long elapsedTime = dateProvider.now().getTime() - event.getSession().getStartTime().getTime();
		if (elapsedTime < 0) {
			throw new TimelineExportException("Relative time is negative");
		}
		return elapsedTime;
	}

}