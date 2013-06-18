package com.lassekoskela.maven.timeline;

import java.util.Comparator;
import java.util.LinkedList;

import org.codehaus.plexus.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ImmutableSortedSet.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeMultiset;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.lassekoskela.maven.bean.Goal;
import com.lassekoskela.maven.bean.Phase;
import com.lassekoskela.maven.bean.Project;
import com.lassekoskela.maven.bean.Timeline;


public class GoalOrganizer {
	
	public static final int COLUMN_WIDTH_PIXEL = 60;
	public static final int HEIGHT_SECOND_SCALING_PIXEL = 4;
	public static final double HEIGHT_MSECOND_SCALING_PIXEL = HEIGHT_SECOND_SCALING_PIXEL / 1000d;
	
	private final Logger logger;
	
	public GoalOrganizer(Logger logger) {
		this.logger = logger;
	}
	
	public Iterable<DisplayableGoal> organize(Timeline timeline) {
		ImmutableList.Builder<DisplayableGoal> goals = ImmutableList.builder();
		Columns columnOfGoals = new Columns();
		
		Iterable<SortedGoal> sortedGoalsByStartTime = sortedGoalsByStartTime(timeline);
		logGoals(sortedGoalsByStartTime);
		for (SortedGoal sortedGoal : sortedGoalsByStartTime) {
			goals.add(buildDisplayableGoal(columnOfGoals, sortedGoal));
		}
		return goals.build();
	}

	private void logGoals(Iterable<SortedGoal> sortedGoalsByStartTime) {
		String formatColumns = "%-70s%15s%15s%15s";
		logger.info(String.format(formatColumns, "Goal", "start time", "duration", "end time"));
		for (SortedGoal sortedGoal : sortedGoalsByStartTime) {
			Goal goal = sortedGoal.goal;
			logger.info(String.format(formatColumns, sortedGoal.project.getItemId() + " " + sortedGoal.phase.getItemId() + " " + goal.getItemId(),
					goal.getStartTimeInMs(), goal.getDuration().toString(), goal.getCompletedTimeInMs()));
		}
	}

	@VisibleForTesting Iterable<SortedGoal> sortedGoalsByStartTime(Timeline timeline) {
		Builder<SortedGoal> sortingGoalSet = ImmutableSortedSet.orderedBy(newGoalStartTimeComparator());
		for (Project project : timeline.getProjects()) {
			for (Phase phase : project.getPhases()) {
				for (Goal goal : phase.getGoals()) {
					sortingGoalSet.add(new SortedGoal(project, phase, goal));
				}
			}
		}
		return sortingGoalSet.build();
	}

	@VisibleForTesting Comparator<SortedGoal> newGoalStartTimeComparator() {
		return new Comparator<SortedGoal>() {
			@Override
			public int compare(SortedGoal o1, SortedGoal o2) {
				if (o1.goal.getStartTimeInMs() != o2.goal.getStartTimeInMs()) {
					return Longs.compare(o1.goal.getStartTimeInMs(), o2.goal.getStartTimeInMs());
				}
				return Longs.compare(o1.goal.getCompletedTimeInMs(), o2.goal.getCompletedTimeInMs());
			}
		};
	}

	@VisibleForTesting DisplayableGoal buildDisplayableGoal(Columns columnOfGoals, SortedGoal sortedGoal) {
		return new DisplayableGoal(
				sortedGoal.project.getItemId(),
				sortedGoal.phase.getItemId(),
				sortedGoal.goal.getItemId(),
				sortedGoal.goal.serializeDependencies(),
				columnToLeftPosition(appendGoalInRightColumn(columnOfGoals, sortedGoal.goal)),
				goalTopPosition(sortedGoal.goal),
				goalHeightValue(sortedGoal.goal));
	}
	
	private long goalHeightValue(Goal goal) {
		return verticalValue(goal.getDuration().inMillis());
	}

	private long goalTopPosition(Goal goal) {
		return verticalValue(goal.getStartTimeInMs());
	}
	
	@VisibleForTesting long verticalValue(long millis) {
		Preconditions.checkArgument(millis >= 0);
		return Math.round(Math.ceil(millis * HEIGHT_MSECOND_SCALING_PIXEL));
	}

	@VisibleForTesting long columnToLeftPosition(int goalColumn) {
		Preconditions.checkArgument(goalColumn >= 0);
		return goalColumn * COLUMN_WIDTH_PIXEL;
	}

	private int appendGoalInRightColumn(Columns columnOfGoals, Goal goal) {
		return columnOfGoals.addGoal(goal);
	}
	
	@VisibleForTesting static class Column implements Comparable<Column> {
		
		private final int index;
		private final LinkedList<Goal> goals;
		
		@VisibleForTesting Column(int index) {
			this.index = index;
			goals = Lists.newLinkedList();
		}
		
		public void addGoal(Goal goal) {
			goals.add(goal);
		}
		
		public long lastGoalEnd() {
			if (goals.isEmpty()) {
				return 0;
			}
			return goals.getLast().getCompletedTimeInMs();
		}

		@Override
		public int compareTo(Column o) {
			int comparingGoalEnd = Longs.compare(lastGoalEnd(), o.lastGoalEnd());
			if (comparingGoalEnd == 0) {
				return Ints.compare(this.index, o.index);
			}
			return comparingGoalEnd;
		}

		public boolean goalFits(Goal goal) {
			return goal.getStartTimeInMs() >= lastGoalEnd();
		}
	}
	
	@VisibleForTesting static class Columns {
		
		private TreeMultiset<Column> columns;
		
		@VisibleForTesting Columns() {
			columns = TreeMultiset.create();
			columns.add(newColumn());
		}

		private Column newColumn() {
			return new Column(columns.size());
		}
		
		public int addGoal(Goal goal) {
			Column column = selectColumn(goal);
			addGoalToColumn(goal, column);
			return column.index;
		}

		private Column selectColumn(Goal goal) {
			Column earliestEndingColumn = columns.firstEntry().getElement();
			if (earliestEndingColumn.goalFits(goal)) {
				return columns.pollFirstEntry().getElement();
			}
			return newColumn();
		}
		
		private void addGoalToColumn(Goal goal, Column column) {
			column.addGoal(goal);
			columns.add(column);
		}
	}

	@VisibleForTesting static class SortedGoal {
		
		private final Project project;
		private final Phase phase;
		private final Goal goal;

		@VisibleForTesting SortedGoal(Project project, Phase phase, Goal goal) {
			this.project = project;
			this.phase = phase;
			this.goal = goal;
		}

		@Override
		public int hashCode(){
			return Objects.hashCode(project, phase, goal);
		}
		
		@Override
		public boolean equals(Object object){
			if (object instanceof SortedGoal) {
				SortedGoal that = (SortedGoal) object;
				return Objects.equal(this.project, that.project)
					&& Objects.equal(this.phase, that.phase)
					&& Objects.equal(this.goal, that.goal);
			}
			return false;
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this)
				.add("project", project)
				.add("phase", phase)
				.add("goal", goal)
				.toString();
		}
	}

	public static class DisplayableGoal {
		
		private final String projectId;
		private final String phaseId;
		private final String goalId;
		private final String dependencies;
		private final long leftPosition;
		private final long topPosition;
		private final long heightPosition;

		@VisibleForTesting DisplayableGoal(String projectId, String phaseId, String goalId, String dependencies,
				long leftPosition, long topPosition, long heightPosition) {
			
			this.projectId = projectId;
			this.phaseId = phaseId;
			this.goalId = goalId;
			this.dependencies = dependencies;
			this.leftPosition = leftPosition;
			this.topPosition = topPosition;
			this.heightPosition = heightPosition;
		}

		public String getProjectId() {
			return projectId;
		}

		public String getPhaseId() {
			return phaseId;
		}

		public String getGoalId() {
			return goalId;
		}

		public String getDependencies() {
			return dependencies;
		}

		public long getLeftPosition() {
			return leftPosition;
		}

		public long getTopPosition() {
			return topPosition;
		}

		public long getHeightPosition() {
			return heightPosition;
		}

		@Override
		public int hashCode(){
			return Objects.hashCode(projectId, phaseId, goalId, dependencies, leftPosition, topPosition, heightPosition);
		}
		
		@Override
		public boolean equals(Object object){
			if (object instanceof DisplayableGoal) {
				DisplayableGoal that = (DisplayableGoal) object;
				return Objects.equal(this.projectId, that.projectId)
					&& Objects.equal(this.phaseId, that.phaseId)
					&& Objects.equal(this.goalId, that.goalId)
					&& Objects.equal(this.dependencies, that.dependencies)
					&& Objects.equal(this.leftPosition, that.leftPosition)
					&& Objects.equal(this.topPosition, that.topPosition)
					&& Objects.equal(this.heightPosition, that.heightPosition);
			}
			return false;
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this)
				.add("projectId", projectId)
				.add("phaseId", phaseId)
				.add("goalId", goalId)
				.add("dependencies", dependencies)
				.add("leftPosition", leftPosition)
				.add("topPosition", topPosition)
				.add("heightPosition", heightPosition)
				.toString();
		}
	}
	
}
