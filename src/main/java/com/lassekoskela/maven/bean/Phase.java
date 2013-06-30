package com.lassekoskela.maven.bean;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class Phase extends MavenItem {

	private final List<Goal> goals;

	public Phase(String name, List<Goal> goals) {
		super(name);
		this.goals = new ArrayList<Goal>(goals);
	}

	public List<Goal> getGoals() {
		return goals;
	}

	public void addGoal(Goal goal) {
		goals.add(goal);
	}

	public Optional<Goal> getGoal(final String goalName) {
		return FluentIterable.from(goals).firstMatch(new Predicate<Goal>() {
			@Override
			public boolean apply(Goal input) {
				return input.getItemId().equals(goalName);
			}
		});
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), goals);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Phase) {
			Phase that = (Phase) object;
			return super.equals(object) && Objects.equal(this.goals, that.goals);
		}
		return false;
	}

	@Override
	public ToStringHelper toStringHelper() {
		return super.toStringHelper().add("goals", goals);
	}
}
