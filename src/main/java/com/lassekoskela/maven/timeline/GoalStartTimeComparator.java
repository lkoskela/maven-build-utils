package com.lassekoskela.maven.timeline;

import java.util.Comparator;

import com.google.common.primitives.Longs;
import com.lassekoskela.maven.timeline.GoalOrganizer.SortedGoal;

public class GoalStartTimeComparator implements Comparator<SortedGoal> {
	public int compare(SortedGoal o1, SortedGoal o2) {
		if (o1.goal.getStartTimeInMs() != o2.goal.getStartTimeInMs()) {
			return Longs.compare(o1.goal.getStartTimeInMs(), o2.goal.getStartTimeInMs());
		}
		return Longs.compare(o1.goal.getCompletedTimeInMs(), o2.goal.getCompletedTimeInMs());
	}
}