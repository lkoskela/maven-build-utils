package com.lassekoskela.maven.bean;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.lassekoskela.time.Duration;


public class Goal extends MavenItem {

	private final long startTimeInMs;

	public Goal(String name, Duration duration, long startTimeInMs) {
		super(name, duration);
		this.startTimeInMs = startTimeInMs;
	}

	public long getStartTimeInMs() {
		return startTimeInMs;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(super.hashCode(), startTimeInMs);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof Goal) {
			Goal that = (Goal) object;
			return super.equals(object)
				&& Objects.equal(this.startTimeInMs, that.startTimeInMs);
		}
		return false;
	}

	@Override
	public ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("startTimeInMs", startTimeInMs);
	}
}
