package com.lassekoskela.maven.bean;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.lassekoskela.time.Duration;

public class MavenItem {

	private final String name;
	private final Duration duration;
	
	public MavenItem(String name, Duration duration) {
		this.name = name;
		this.duration = duration;
	}
	
	public String getItemId() {
		return name;
	}
	
	public Duration getDuration() {
		return duration;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(name, duration);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof MavenItem) {
			MavenItem that = (MavenItem) object;
			return Objects.equal(this.name, that.name)
				&& Objects.equal(this.duration, that.duration);
		}
		return false;
	}

	@Override
	public String toString() {
		return toStringHelper()
			.toString();
	}
	
	protected ToStringHelper toStringHelper() {
		return Objects.toStringHelper(this)
			.add("name", name)
			.add("duration", duration);
	}
}
