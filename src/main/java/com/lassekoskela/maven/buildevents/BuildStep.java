package com.lassekoskela.maven.buildevents;

import com.lassekoskela.time.Clock;
import com.lassekoskela.time.Duration;

public class BuildStep {
	public final String phase, groupId, artifactId, goal;
	private long startedAt, endedAt;

	public BuildStep(String phase, String groupId, String artifactId,
			String goal) {
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
}