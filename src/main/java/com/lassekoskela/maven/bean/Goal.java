package com.lassekoskela.maven.bean;

import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.lassekoskela.time.Duration;


public class Goal extends MavenItem {

	private final long startTimeInMs;
	private final Set<String> dependencies;
	private Duration duration;

	public Goal(String name, Duration duration, long startTimeInMs, Set<String> dependencies) {
		super(name);
		this.duration = duration;
		this.startTimeInMs = startTimeInMs;
		this.dependencies = dependencies;
	}

	public long getStartTimeInMs() {
		return startTimeInMs;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	
	public Set<String> getDependencies() {
		return dependencies;
	}
	
	public String serializeDependencies() {
		return Joiner.on(' ').join(dependencies);
	}

	public long getCompletedTimeInMs() {
		return startTimeInMs + getDuration().inMillis();
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(super.hashCode(), duration, startTimeInMs, dependencies);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof Goal) {
			Goal that = (Goal) object;
			return super.equals(object)
				&& Objects.equal(this.duration, that.duration)
				&& Objects.equal(this.startTimeInMs, that.startTimeInMs)
				&& Objects.equal(this.dependencies, that.dependencies);
		}
		return false;
	}

	@Override
	public ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("duration", duration)
			.add("startTimeInMs", startTimeInMs)
			.add("dependencies", dependencies);
	}
}
