package com.lassekoskela.maven.bean;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.lassekoskela.time.Duration;

public class Project extends MavenItem {

	private final Set<Phase> phases;
	
	public Project(String name, Duration duration, Set<Phase> phases) {
		super(name, duration);
		this.phases = phases;
	}
	
	public Set<Phase> getPhases() {
		return phases;
	}
	
	public void addPhase(Phase phase) {
		phases.add(phase);
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
