package com.lassekoskela.maven.bean;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class Project extends MavenItem {

	private final Set<Phase> phases;
	
	public Project(String name, Set<Phase> phases) {
		super(name);
		this.phases = phases;
	}
	
	public Set<Phase> getPhases() {
		return phases;
	}
	
	public Phase addPhase(Phase phase) {
		phases.add(phase);
		return phase;
	}

	public Optional<Phase> getPhase(final String phaseName) {
		return FluentIterable
				.from(phases)
				.firstMatch(new Predicate<Phase>() {
					@Override
					public boolean apply(Phase input) {
						return input.getItemId().equals(phaseName);
					}
				});
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(super.hashCode(), phases);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Project) {
			Project that = (Project) object;
			return super.equals(object)
				&& Objects.equal(this.phases, that.phases);
		}
		return false;
	}

	@Override
	public ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("phases", phases);
	}
}
