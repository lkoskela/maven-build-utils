package com.lassekoskela.maven.bean;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.lassekoskela.time.Duration;


public class Goal extends MavenItem {

	private final long startTimeInMs;
	private Duration duration;

	public Goal(String name, Duration duration, long startTimeInMs) {
		super(name);
		this.duration = duration;
		this.startTimeInMs = startTimeInMs;
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

	@Override
	public final int hashCode(){
		return Objects.hashCode(super.hashCode(), duration, startTimeInMs);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof Goal) {
			Goal that = (Goal) object;
			return super.equals(object)
				&& Objects.equal(this.duration, that.duration)
				&& Objects.equal(this.startTimeInMs, that.startTimeInMs);
		}
		return false;
	}

	@Override
	public ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("duration", duration)
			.add("startTimeInMs", startTimeInMs);
	}
}
