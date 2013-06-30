package com.lassekoskela.maven.timeline;

import static com.lassekoskela.maven.timeline.ObjectBuilder.goal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import com.lassekoskela.maven.timeline.GoalOrganizer.SortedGoal;

public class GoalStartTimeComparatorTest {
	private Comparator<SortedGoal> comparator;

	@Before
	public void setUp() throws Exception {
		comparator = new GoalStartTimeComparator();
	}

	private SortedGoal sortedGoal(long duration, long startTime) {
		return new SortedGoal(null, null, goal("g", duration, startTime));
	}

	@Test
	public void sortsGoalsByStartTime() {
		assertThat(comparator.compare(sortedGoal(0, 0), sortedGoal(0, 100)), is(-1));
		assertThat(comparator.compare(sortedGoal(0, 100), sortedGoal(0, 0)), is(1));
	}

	@Test
	public void durationsDoNotMatterIfStartTimesDiffer() {
		assertThat(comparator.compare(sortedGoal(10, 0), sortedGoal(20, 100)), is(-1));
		assertThat(comparator.compare(sortedGoal(20, 0), sortedGoal(10, 100)), is(-1));
	}

	@Test
	public void sortsByDurationIfStartTimesAreTheSame() {
		assertThat(comparator.compare(sortedGoal(100, 0), sortedGoal(200, 0)), is(-1));
		assertThat(comparator.compare(sortedGoal(300, 0), sortedGoal(200, 0)), is(1));
	}

	@Test
	public void bothAreZeroDurationAndHaveSameStartTime() {
		assertThat(comparator.compare(sortedGoal(0, 0), sortedGoal(0, 0)), is(0));
	}
}