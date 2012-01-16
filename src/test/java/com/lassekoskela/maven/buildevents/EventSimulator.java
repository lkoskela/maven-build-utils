package com.lassekoskela.maven.buildevents;

import com.lassekoskela.time.Clock;

public class EventSimulator {
	private final BuildEventLog log;
	public String project = "project";
	public String phase = "phase";
	public String groupId = "group";
	public String artifactId = "artifact";

	public EventSimulator(BuildEventLog log) {
		this.log = log;
	}

	public void goal(String project, String phase, String goal,
			long durationInMillis) {
		this.project = project;
		this.phase = phase;
		log.start(project, phase, groupId, artifactId, goal);
		Clock.advance(durationInMillis);
		log.end(project, phase, groupId, artifactId, goal);
	}

	public void goal(String phase, String goal, long durationInMillis) {
		goal(this.project, phase, goal, durationInMillis);
	}

	public void goal(String goal, long durationInMillis) {
		goal(this.project, this.phase, goal, durationInMillis);
	}
}