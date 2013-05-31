package com.lassekoskela.maven.bean;

import com.lassekoskela.time.Duration;


public class Goal extends MavenItem {

	public Goal(String name, Duration duration) {
		super(name, duration);
	}

	@Override
	public final int hashCode(){
		return super.hashCode();
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof Goal) {
			return super.equals(object);
		}
		return false;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
