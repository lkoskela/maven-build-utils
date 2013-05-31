package com.lassekoskela.maven.bean;

import java.util.Set;

import com.google.common.base.Objects;

public class Timeline {

	private final Set<Project> projects;
	
	public Timeline(Set<Project> projects) {
		this.projects = projects;
	}
	
	public Set<Project> getProjects() {
		return projects;
	}
	
	public void addProject(Project project) {
		projects.add(project);
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
