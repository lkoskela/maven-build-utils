package com.lassekoskela.maven.timeline;

import static com.lassekoskela.maven.timeline.GoalOrganizer.COLUMN_WIDTH_PIXEL;
import static com.lassekoskela.maven.timeline.ObjectBuilder.goal;
import static com.lassekoskela.maven.timeline.ObjectBuilder.phase;
import static com.lassekoskela.maven.timeline.ObjectBuilder.project;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.lassekoskela.maven.bean.Goal;
import com.lassekoskela.maven.bean.Timeline;
import com.lassekoskela.maven.timeline.GoalOrganizer.Columns;
import com.lassekoskela.maven.timeline.GoalOrganizer.DisplayableGoal;
import com.lassekoskela.maven.timeline.GoalOrganizer.SortedGoal;

public class GoalOrganizerTest {

	private ConsoleLogger logger;
	private GoalOrganizer goalOrganizer;

	@Before
	public void setUp() {
		logger = new ConsoleLogger();
		goalOrganizer = new GoalOrganizer(logger);
	}

	@Test(expected = IllegalArgumentException.class)
	public void leftPositionForColumnWithNegativeNumberShouldRaiseAnException() {
		goalOrganizer.leftPositionForColumn(-1);
	}

	@Test
	public void leftPositionForFirstColumnIsZero() {
		assertThat(goalOrganizer.leftPositionForColumn(0), is(0));
	}

	@Test
	public void leftPositionForSubsequentColumnsAreMultipliedByColumnWidth() {
		assertThat(goalOrganizer.leftPositionForColumn(1), is(COLUMN_WIDTH_PIXEL));
		assertThat(goalOrganizer.leftPositionForColumn(2), is(2 * COLUMN_WIDTH_PIXEL));
		assertThat(goalOrganizer.leftPositionForColumn(3), is(3 * COLUMN_WIDTH_PIXEL));
	}

	@Test(expected = IllegalArgumentException.class)
	public void topPositionForNegativeDurationShouldRaiseAnException() {
		goalOrganizer.topPositionForDuration(-1);
	}

	@Test
	public void topPositionForZeroDurationIsZero() {
		assertThat(goalOrganizer.topPositionForDuration(0), is(0));
	}

	@Test
	public void topPositionIncreasesAlongWithDuration() {
		assertThat(goalOrganizer.topPositionForDuration(900), is(4));
		assertThat(goalOrganizer.topPositionForDuration(1000), is(4));
		assertThat(goalOrganizer.topPositionForDuration(1100), is(5));
		assertThat(goalOrganizer.topPositionForDuration(1000 * 60 * 60), is(14400));
	}

	@Test
	public void buildDisplayableGoalForTrivialZeroLengthOnePhaseOneGoalProject() {
		Columns columnOfGoals = new Columns();
		SortedGoal sortedGoal = new SortedGoal(project("project1"), phase("phase1"), goal("goal1", 0, 0));

		DisplayableGoal displayableGoal = goalOrganizer.buildDisplayableGoal(columnOfGoals, sortedGoal);
		assertThat(displayableGoal, is(new DisplayableGoal("project1", "phase1", "goal1", "", 0, 0, 0)));
	}

	@Test
	public void buildDisplayableGoalWithStartTimeAndDuration() {
		Columns columnOfGoals = new Columns();
		Goal goal = goal("goal1", 2000, 1000, "dep1", "dep2");
		SortedGoal sortedGoal = new SortedGoal(project("project1"), phase("phase1"), goal);

		DisplayableGoal displayableGoal = goalOrganizer.buildDisplayableGoal(columnOfGoals, sortedGoal);
		assertThat(displayableGoal, is(new DisplayableGoal("project1", "phase1", "goal1", "dep1 dep2", 0, 4, 8)));
	}

	@Test
	public void buildSecondDisplayableGoalWhenFittingFirstColumn() {
		Columns columnOfGoals = new Columns();
		columnOfGoals.addGoal(goal("goal1", 2000, 1000));

		Goal goal = goal("goal1", 8000, 4000, "dep1", "dep2");
		SortedGoal sortedGoal = new SortedGoal(project("project1"), phase("phase1"), goal);

		DisplayableGoal displayableGoal = goalOrganizer.buildDisplayableGoal(columnOfGoals, sortedGoal);
		assertThat(displayableGoal, is(new DisplayableGoal("project1", "phase1", "goal1", "dep1 dep2", 0, 16, 32)));
	}

	@Test
	public void buildSecondDisplayableGoalWhenNotFittingFirstColumn() {
		Columns columnOfGoals = new Columns();
		columnOfGoals.addGoal(goal("goal1", 2000, 1000));

		Goal goal = goal("goal1", 2000, 2000, "dep1", "dep2");
		SortedGoal sortedGoal = new SortedGoal(project("project1"), phase("phase1"), goal);

		DisplayableGoal displayableGoal = goalOrganizer.buildDisplayableGoal(columnOfGoals, sortedGoal);
		assertThat(displayableGoal, is(new DisplayableGoal("project1", "phase1", "goal1", "dep1 dep2", 60, 8, 8)));
	}

	@Test
	public void testOrganizeTimelineTwoProjects() throws Exception {
		Timeline timeline = new Timeline(ImmutableSet.of(
				project("project1",
						phase("phase1", goal("goal1", 800, 0), goal("goal3", 0, 400), goal("goal2", 400, 0)),
						phase("phase2", goal("goal1", 1200, 1000, "project1-phase1-goal1"),
								goal("goal2", 2400, 2000, "project1-phase1-goal1", "project1-phase1-goal2"))),
				project("project2",
						phase("phase1", goal("goal1", 200, 5500), goal("goal2", 400, 15000, "project2-phase1-goal1")),
						phase("phase2", goal("goal1", 1200, 30000, "project2-phase2-goal2", "project2-phase1-goal2"),
								goal("goal2", 400, 28000)))));

		assertThat(
				goalOrganizer.organize(timeline),
				contains(new DisplayableGoal("project1", "phase1", "goal2", "", 0, 0, 2), new DisplayableGoal(
						"project1", "phase1", "goal1", "", 60, 0, 4), new DisplayableGoal("project1", "phase1",
						"goal3", "", 0, 2, 0), new DisplayableGoal("project1", "phase2", "goal1",
						"project1-phase1-goal1", 0, 4, 5), new DisplayableGoal("project1", "phase2", "goal2",
						"project1-phase1-goal1 project1-phase1-goal2", 60, 8, 10), new DisplayableGoal("project2",
						"phase1", "goal1", "", 0, 22, 1), new DisplayableGoal("project2", "phase1", "goal2",
						"project2-phase1-goal1", 60, 60, 2), new DisplayableGoal("project2", "phase2", "goal2", "", 0,
						112, 2), new DisplayableGoal("project2", "phase2", "goal1",
						"project2-phase2-goal2 project2-phase1-goal2", 60, 120, 5)));
	}

	@Test
	public void testOrganizeTimelineWhenTwoGoalsHaveSameCompletedTime() throws Exception {
		Timeline timeline = new Timeline(ImmutableSet.of(
				project("parent", phase("clean", goal("clean", 191, 1231)),
						phase("install", goal("install", 144, 1300))),
				project("technical-log-bean", phase("clean", goal("clean", 6, 1473)),
						phase("process-resources", goal("resources", 0, 1479))),
				project("common-test", phase("clean", goal("clean", 87, 1514)),
						phase("process-resources", goal("resources", 16, 1601)))));

		assertThat(
				goalOrganizer.organize(timeline),
				contains(new DisplayableGoal("parent", "clean", "clean", "", 0, 5, 1), new DisplayableGoal("parent",
						"install", "install", "", 60, 6, 1), new DisplayableGoal("technical-log-bean", "clean",
						"clean", "", 0, 6, 1), new DisplayableGoal("technical-log-bean", "process-resources",
						"resources", "", 60, 6, 0), new DisplayableGoal("common-test", "clean", "clean", "", 0, 7, 1),
						new DisplayableGoal("common-test", "process-resources", "resources", "", 60, 7, 1)));
	}
}
