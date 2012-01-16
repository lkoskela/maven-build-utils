package com.lassekoskela.maven.buildevents;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;

import com.lassekoskela.time.Clock;

public abstract class AbstractBuildEventLogReportTest {

	protected static Matcher<String> containsInOrder(final String... strings) {
		return new BaseMatcher<String>() {

			@Override
			public boolean matches(Object candidate) {
				if (candidate == null
						|| !candidate.getClass().equals(String.class)) {
					return false;
				}
				String haystack = shrinkWhitespace((String) candidate);
				for (String needle : strings) {
					needle = shrinkWhitespace(needle);
					int index = haystack.indexOf(needle);
					if (index == -1) {
						return false;
					}
					haystack = haystack.substring(index + needle.length());
				}
				return true;
			}

			private String shrinkWhitespace(String s) {
				return s.replaceAll("( |\t)+", " ");
			}

			@Override
			public void describeTo(Description d) {
				d.appendText("contains in order: " + asList(strings));
			}
		};
	}

	protected FakeLogger logger;
	protected BuildEventLogReport report;

	@Before
	public final void _createFixtureObjects() throws Exception {
		Clock.freeze();
		logger = new FakeLogger();
	}

	@After
	public final void _tearDownFixtureObjects() throws Exception {
		Clock.reset();
	}

	protected BuildStep step(String project, String phase, String goal,
			long duration) {
		BuildStep step = new BuildStep(project, phase, "group", "artifact",
				goal);
		step.start();
		Clock.advance(duration);
		step.end();
		return step;
	}

	protected BuildStep step(String phase, String goal, long duration) {
		return step("project", phase, goal, duration);
	}

	protected String goalStatsLine(String goal, double durationInSeconds,
			double percentageOfPhase) {
		String duration = durationString(durationInSeconds);
		String percentage = percentageString(percentageOfPhase);
		return " artifact:" + goal + " " + duration + " " + percentage;
	}

	protected String phaseStatsLine(String phase, double durationInSeconds,
			double percentageOfTotal) {
		String duration = durationString(durationInSeconds);
		String percentage = percentageString(percentageOfTotal);
		return phase + " " + duration + " " + percentage;
	}

	protected String projectStatsLine(String project, double durationInSeconds,
			double percentageOfTotal) {
		String duration = durationString(durationInSeconds);
		String percentage = percentageString(percentageOfTotal);
		return "*" + project + " " + duration + " " + percentage;
	}

	private String percentageString(double percentageOfPhase) {
		return format("%.0f", percentageOfPhase) + "%";
	}

	private String durationString(double durationInSeconds) {
		return format("%.1f", durationInSeconds) + "s";
	}
}
