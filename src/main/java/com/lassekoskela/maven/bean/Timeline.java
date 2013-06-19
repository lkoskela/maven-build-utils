package com.lassekoskela.maven.bean;

import com.google.common.base.Objects;

public class Timeline {

	private final Iterable<Project> projects;
	
	public Timeline(Iterable<Project> projects) {
		this.projects = projects;
	}
	
	public Iterable<Project> getProjects() {
		return projects;
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(projects);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Timeline) {
			Timeline that = (Timeline) object;
			return Objects.equal(this.projects, that.projects);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("projects", projects)
			.toString();
	}
}
