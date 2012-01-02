package com.lassekoskela.time;

import com.lassekoskela.time.Duration;

public class DurationBuilder {

	public static DurationBuilder newDuration() {
		return new DurationBuilder();
	}

	private long totalMilliseconds;

	public Duration build() {
		return new Duration(totalMilliseconds);
	}

	public DurationBuilder hours(int numberOfHours) {
		totalMilliseconds += (numberOfHours * 60 * 60 * 1000);
		return this;
	}

	public DurationBuilder minutes(int numberOfMinutes) {
		totalMilliseconds += (numberOfMinutes * 60 * 1000);
		return this;
	}

	public DurationBuilder seconds(int numberOfSeconds) {
		totalMilliseconds += (numberOfSeconds * 1000);
		return this;
	}

	public DurationBuilder millis(int numberOfMilliseconds) {
		totalMilliseconds += numberOfMilliseconds;
		return this;
	}

}