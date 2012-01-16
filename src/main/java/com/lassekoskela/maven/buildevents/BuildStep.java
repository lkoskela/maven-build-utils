package com.lassekoskela.maven.buildevents;

import com.lassekoskela.time.Clock;
import com.lassekoskela.time.Duration;

public class BuildStep {
	public final String project, phase, groupId, artifactId, goal;
	private long startedAt, endedAt;

	public BuildStep(String project, String phase, String groupId, String artifactId,
			String goal) {
		this.project = project;
		this.phase = phase;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.goal = goal;
	}

	public Duration duration() {
		return new Duration(endedAt - startedAt);
	}

	public void end() {
		this.endedAt = Clock.now();
	}

	public void start() {
		startedAt = Clock.now();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(getClass())) {
			return false;
		}
		return toString().equals(obj.toString());
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(getClass().getSimpleName()).append("(");
		s.append(project).append("/").append(phase).append("/");
		s.append(groupId).append(":").append(artifactId);
		s.append("/").append(goal).append(")");
		return s.toString();
	}
}