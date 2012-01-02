package com.lassekoskela.time;

public class Duration {
	private static final int MILLIS_IN_HOUR = 60 * 60 * 1000;
	private static final int MILLIS_IN_MINUTE = 60 * 1000;
	private static final int MILLIS_IN_SECOND = 1000;

	private long hours;
	private long minutes;
	private long seconds;
	private long millis;
	private final long inMilliseconds;

	public Duration(long milliseconds) {
		inMilliseconds = milliseconds;
		calculateTimeUnitsFrom(milliseconds);
	}

	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().equals(getClass())) {
			return false;
		}
		return this.inMilliseconds == ((Duration) obj).inMilliseconds;
	}

	private void calculateTimeUnitsFrom(long remainingMillis) {
		// hours
		hours = remainingMillis / MILLIS_IN_HOUR;
		remainingMillis -= (hours * MILLIS_IN_HOUR);
		// minutes
		minutes = remainingMillis / MILLIS_IN_MINUTE;
		remainingMillis -= (minutes * MILLIS_IN_MINUTE);
		// seconds
		seconds = remainingMillis / MILLIS_IN_SECOND;
		remainingMillis -= (seconds * MILLIS_IN_SECOND);
		// milliseconds
		millis = remainingMillis;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if (hours > 0) {
			s.append(" ").append(hours).append("h");
		}
		if (minutes > 0) {
			s.append(" ").append(minutes).append("m");
		}
		if (seconds > 0) {
			s.append(" ").append(seconds).append("s");
		}
		if (millis > 0) {
			s.append(" ").append(millis).append("ms");
		}
		return s.toString().trim();
	}

	public long inMillis() {
		return inMilliseconds;
	}

	public double inSeconds() {
		return inMillis() / 1000.0;
	}

	public double percentageOf(long milliseconds) {
		return (inMillis() * 100.0) / milliseconds;
	}

	public double percentageOf(Duration duration) {
		return percentageOf(duration.inMillis());
	}
}