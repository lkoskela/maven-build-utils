package com.lassekoskela.maven.bean;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.lassekoskela.time.Duration;

public class Phase extends MavenItem {

	private final Set<Goal> goals;
	
	public Phase(String name, Duration duration, Set<Goal> goals) {
		super(name, duration);
		this.goals = goals;
	}
	
	public Set<Goal> getGoals() {
		return goals;
	}
	
	public void addGoal(Goal goal) {
		goals.add(goal);
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(super.hashCode(), goals);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Phase) {
			Phase that = (Phase) object;
			return super.equals(object)
				&& Objects.equal(this.goals, that.goals);
		}
		return false;
	}

	@Override
	public ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("goals", goals);
	}
}
