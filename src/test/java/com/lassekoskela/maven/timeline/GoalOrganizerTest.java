package com.lassekoskela.maven.timeline;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.util.Comparator;
import java.util.SortedSet;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.lassekoskela.maven.bean.Goal;
import com.lassekoskela.maven.bean.Phase;
import com.lassekoskela.maven.bean.Project;
import com.lassekoskela.maven.bean.Timeline;
import com.lassekoskela.maven.timeline.GoalOrganizer.Columns;
import com.lassekoskela.maven.timeline.GoalOrganizer.DisplayableGoal;
import com.lassekoskela.maven.timeline.GoalOrganizer.SortedGoal;
import com.lassekoskela.time.Duration;


public class GoalOrganizerTest {

	
	
	private ConsoleLogger logger;
	private GoalOrganizer testee;

	@Before
	public void setUp() {
		logger = new ConsoleLogger();
		testee = new GoalOrganizer(logger);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testColumnToLeftPositionWhenNegative() {
		testee.columnToLeftPosition(-1);
	}
	
	@Test
	public void testColumnToLeftPositionWhenZero() {
		assertThat(testee.columnToLeftPosition(0), is(0L));
	}
	
	@Test
	public void testColumnToLeftPositionWhenOne() {
		assertThat(testee.columnToLeftPosition(1), is(60L));
	}
	
	@Test
	public void testColumnToLeftPositionWhenThousand() {
		assertThat(testee.columnToLeftPosition(1000), is(60000L));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testVerticalValueWhenNegative() {
		testee.verticalValue(-1l);
	}
	
	@Test
	public void testVerticalValueWhenZero() {
		assertThat(testee.verticalValue(0l), is(0L));
	}

	@Test
	public void testVerticalValueWhenCloseToOneSecond() {
		assertThat(testee.verticalValue(900l), is(4L));
	}

	@Test
	public void testVerticalValueWhenAlmostToOneSecond() {
		assertThat(testee.verticalValue(1100l), is(5L));
	}

	@Test
	public void testVerticalValueWhenOneSecond() {
		assertThat(testee.verticalValue(1000l), is(4L));
	}

	@Test
	public void testVerticalValueWhenOneHour() {
		assertThat(testee.verticalValue(1000l * 60 * 60), is(14400L));
	}
	
	@Test
	public void testBuildMinimalDisplayableGoal() {
		Columns columnOfGoals = new Columns();
		Project project = new Project("project1", ImmutableSet.<Phase>of());
		Phase phase = new Phase("phase1", ImmutableSet.<Goal>of());
		Goal goal = new Goal("goal1", new Duration(0), 0, ImmutableSet.<String>of());
		SortedGoal sortedGoal = new SortedGoal(project, phase, goal);

		assertThat(testee.buildDisplayableGoal(columnOfGoals, sortedGoal), is(
				new DisplayableGoal("project1", "phase1", "goal1", "", 0, 0, 0)));
	}
	
	@Test
	public void testBuildFirstDisplayableGoal() {
		Columns columnOfGoals = new Columns();
		Project project = new Project("project1", ImmutableSet.<Phase>of());
		Phase phase = new Phase("phase1", ImmutableSet.<Goal>of());
		Goal goal = new Goal("goal1", new Duration(2000), 1000, ImmutableSet.of("dep1", "dep2"));
		SortedGoal sortedGoal = new SortedGoal(project, phase, goal);

		assertThat(testee.buildDisplayableGoal(columnOfGoals, sortedGoal), is(
				new DisplayableGoal("project1", "phase1", "goal1", "dep1 dep2", 0, 4, 8)));
	}
	
	@Test
	public void testBuildSecondDisplayableGoalWhenFittingFirstColumn() {
		Columns columnOfGoals = new Columns();
		columnOfGoals.addGoal(new Goal("goal1", new Duration(2000), 1000, ImmutableSet.<String>of()));
		
		Project project = new Project("project1", ImmutableSet.<Phase>of());
		Phase phase = new Phase("phase1", ImmutableSet.<Goal>of());
		Goal goal = new Goal("goal1", new Duration(8000), 4000, ImmutableSet.of("dep1", "dep2"));
		SortedGoal sortedGoal = new SortedGoal(project, phase, goal);

		assertThat(testee.buildDisplayableGoal(columnOfGoals, sortedGoal), is(
				new DisplayableGoal("project1", "phase1", "goal1", "dep1 dep2", 0, 16, 32)));
	}
	
	@Test
	public void testBuildSecondDisplayableGoalWhenNotFittingFirstColumn() {
		Columns columnOfGoals = new Columns();
		columnOfGoals.addGoal(new Goal("goal1", new Duration(2000), 1000, ImmutableSet.<String>of()));
		
		Project project = new Project("project1", ImmutableSet.<Phase>of());
		Phase phase = new Phase("phase1", ImmutableSet.<Goal>of());
		Goal goal = new Goal("goal1", new Duration(2000), 2000, ImmutableSet.of("dep1", "dep2"));
		SortedGoal sortedGoal = new SortedGoal(project, phase, goal);

		assertThat(testee.buildDisplayableGoal(columnOfGoals, sortedGoal), is(
				new DisplayableGoal("project1", "phase1", "goal1", "dep1 dep2", 60, 8, 8)));
	}
	
	@Test
	public void testGoalStartTimeComparatorFirstEarlier() {
		Comparator<SortedGoal> goalStartTimeComparator = testee.newGoalStartTimeComparator();
		SortedGoal goal1 = new SortedGoal(null, null, new Goal("g1", new Duration(0), 0, ImmutableSet.<String>of()));
		SortedGoal goal2 = new SortedGoal(null, null, new Goal("g1", new Duration(0), 100, ImmutableSet.<String>of()));

		assertThat(goalStartTimeComparator.compare(goal1, goal2), is(-1));
	}
	
	@Test
	public void testGoalStartTimeComparatorFirstLater() {
		Comparator<SortedGoal> goalStartTimeComparator = testee.newGoalStartTimeComparator();
		SortedGoal goal1 = new SortedGoal(null, null, new Goal("g1", new Duration(0), 100, ImmutableSet.<String>of()));
		SortedGoal goal2 = new SortedGoal(null, null, new Goal("g1", new Duration(0), 0, ImmutableSet.<String>of()));

		assertThat(goalStartTimeComparator.compare(goal1, goal2), is(1));
	}

	@Test
	public void testGoalStartTimeComparatorBothZero() {
		Comparator<SortedGoal> goalStartTimeComparator = testee.newGoalStartTimeComparator();
		SortedGoal goal1 = new SortedGoal(null, null, new Goal("g1", new Duration(0), 0, ImmutableSet.<String>of()));
		SortedGoal goal2 = new SortedGoal(null, null, new Goal("g1", new Duration(0), 0, ImmutableSet.<String>of()));

		assertThat(goalStartTimeComparator.compare(goal1, goal2), is(0));		
	}
	
	@Test
	public void testGoalStartTimeComparatorTakeDurationWhenSameStartTime() {
		Comparator<SortedGoal> goalStartTimeComparator = testee.newGoalStartTimeComparator();
		SortedGoal goal1 = new SortedGoal(null, null, new Goal("g1", new Duration(100), 0, ImmutableSet.<String>of()));
		SortedGoal goal2 = new SortedGoal(null, null, new Goal("g1", new Duration(200), 0, ImmutableSet.<String>of()));

		assertThat(goalStartTimeComparator.compare(goal1, goal2), is(-1));
	}
	
	@Test
	public void testGoalStartTimeComparatorTakeDurationWhenSameStartTimeSecond() {
		Comparator<SortedGoal> goalStartTimeComparator = testee.newGoalStartTimeComparator();
		SortedGoal goal1 = new SortedGoal(null, null, new Goal("g1", new Duration(300), 0, ImmutableSet.<String>of()));
		SortedGoal goal2 = new SortedGoal(null, null, new Goal("g1", new Duration(200), 0, ImmutableSet.<String>of()));

		assertThat(goalStartTimeComparator.compare(goal1, goal2), is(1));
	}
	
	@Test
	public void testGoalStartTimeComparator() {
		SortedSet<SortedGoal> goals = ImmutableSortedSet.orderedBy(testee.newGoalStartTimeComparator())
			.add(new SortedGoal(null, null, new Goal("g1", new Duration(300), 100, ImmutableSet.<String>of())))
			.add(new SortedGoal(null, null, new Goal("g2", new Duration(200), 100, ImmutableSet.<String>of())))
			.add(new SortedGoal(null, null, new Goal("g3", new Duration(400), 100, ImmutableSet.<String>of())))
			.add(new SortedGoal(null, null, new Goal("g4", new Duration(50), 40, ImmutableSet.<String>of())))
			.add(new SortedGoal(null, null, new Goal("g5", new Duration(60), 40, ImmutableSet.<String>of())))
			.add(new SortedGoal(null, null, new Goal("g6", new Duration(40), 60, ImmutableSet.<String>of())))
			.build();

		assertThat(goals, contains(
				new SortedGoal(null, null, new Goal("g4", new Duration(50), 40, ImmutableSet.<String>of())),
				new SortedGoal(null, null, new Goal("g5", new Duration(60), 40, ImmutableSet.<String>of())),
				new SortedGoal(null, null, new Goal("g6", new Duration(40), 60, ImmutableSet.<String>of())),
				new SortedGoal(null, null, new Goal("g2", new Duration(200), 100, ImmutableSet.<String>of())),
				new SortedGoal(null, null, new Goal("g1", new Duration(300), 100, ImmutableSet.<String>of())),
				new SortedGoal(null, null, new Goal("g3", new Duration(400), 100, ImmutableSet.<String>of()))));
		
	}
	
	@Test
	public void testOrganizeTimelineTwoProjects() throws Exception {
		Timeline timeline = new Timeline(ImmutableSet.of(
			new Project("project1", ImmutableSet.of(
				new Phase("phase1", ImmutableSet.of(
						new Goal("goal1", new Duration(800), 0, ImmutableSet.<String>of()),
						new Goal("goal3", new Duration(0), 400, ImmutableSet.<String>of()),
						new Goal("goal2", new Duration(400), 0, ImmutableSet.<String>of()))),
				new Phase("phase2", ImmutableSet.of(
						new Goal("goal1", new Duration(1200), 1000, ImmutableSet.of("project1-phase1-goal1")),
						new Goal("goal2", new Duration(2400), 2000, ImmutableSet.of("project1-phase1-goal1", "project1-phase1-goal2")))))),
			new Project("project2", ImmutableSet.of(
				new Phase("phase1", ImmutableSet.of(
						new Goal("goal1", new Duration(200), 5500, ImmutableSet.<String>of()),
						new Goal("goal2", new Duration(400), 15000, ImmutableSet.of("project2-phase1-goal1")))),
				new Phase("phase2", ImmutableSet.of(
						new Goal("goal1", new Duration(1200), 30000, ImmutableSet.of("project2-phase2-goal2", "project2-phase1-goal2")),
						new Goal("goal2", new Duration(400), 28000, ImmutableSet.<String>of())))))));
		
		assertThat(testee.organize(timeline), contains(
				new DisplayableGoal("project1", "phase1", "goal2", "", 0, 0, 2),
				new DisplayableGoal("project1", "phase1", "goal1", "", 60, 0, 4),
				new DisplayableGoal("project1", "phase1", "goal3", "", 0, 2, 0),
				new DisplayableGoal("project1", "phase2", "goal1", "project1-phase1-goal1", 0, 4, 5),
				new DisplayableGoal("project1", "phase2", "goal2", "project1-phase1-goal1 project1-phase1-goal2", 60, 8, 10),
				new DisplayableGoal("project2", "phase1", "goal1", "", 0, 22, 1),
				new DisplayableGoal("project2", "phase1", "goal2", "project2-phase1-goal1", 60, 60, 2),
				new DisplayableGoal("project2", "phase2", "goal2", "", 0, 112, 2),
				new DisplayableGoal("project2", "phase2", "goal1", "project2-phase2-goal2 project2-phase1-goal2", 60, 120, 5)
		));
	}

	@Test
	public void testOrganizeTimelineWhenTwoGoalsHaveSameCompletedTime() throws Exception {
		Timeline timeline = new Timeline(ImmutableSet.of(
			new Project("parent", ImmutableSet.of(
				new Phase("clean", ImmutableSet.of(
						new Goal("clean", new Duration(191), 1231, ImmutableSet.<String>of()))),
				new Phase("install", ImmutableSet.of(
						new Goal("install", new Duration(144), 1300, ImmutableSet.<String>of()))))),
			new Project("technical-log-bean", ImmutableSet.of(
				new Phase("clean", ImmutableSet.of(
						new Goal("clean", new Duration(6), 1473, ImmutableSet.<String>of()))),
				new Phase("process-resources", ImmutableSet.of(
						new Goal("resources", new Duration(0), 1479, ImmutableSet.<String>of()))))),
			new Project("common-test", ImmutableSet.of(
					new Phase("clean", ImmutableSet.of(
							new Goal("clean", new Duration(87), 1514, ImmutableSet.<String>of()))),
					new Phase("process-resources", ImmutableSet.of(
							new Goal("resources", new Duration(16), 1601, ImmutableSet.<String>of())))))));

		assertThat(testee.organize(timeline), contains(
				new DisplayableGoal("parent", "clean", "clean", "", 0, 5, 1),
				new DisplayableGoal("parent", "install", "install", "", 60, 6, 1),
				new DisplayableGoal("technical-log-bean", "clean", "clean", "", 0, 6, 1),
				new DisplayableGoal("technical-log-bean", "process-resources", "resources", "", 60, 6, 0),
				new DisplayableGoal("common-test", "clean", "clean", "", 0, 7, 1),
				new DisplayableGoal("common-test", "process-resources", "resources", "", 60, 7, 1)
		));
	}
}
