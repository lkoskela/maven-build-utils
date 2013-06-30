package com.lassekoskela.maven.timeline;

import static java.util.Arrays.asList;

import com.lassekoskela.maven.bean.Goal;
import com.lassekoskela.maven.bean.Phase;
import com.lassekoskela.maven.bean.Project;
import com.lassekoskela.maven.bean.Timeline;
import com.lassekoskela.time.Duration;

public class ObjectBuilder {
	public static Timeline timeline(Project... projects) {
		return new Timeline(asList(projects));
	}

	public static Project project(String name, Phase... phases) {
		return new Project(name, asList(phases));
	}

	public static Phase phase(String name, Goal... goals) {
		return new Phase(name, asList(goals));
	}

	public static Goal goal(String name, long duration, long startTime, String... dependencies) {
		return new Goal(name, new Duration(duration), startTime, asList(dependencies));
	}
}