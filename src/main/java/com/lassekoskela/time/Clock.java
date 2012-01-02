package com.lassekoskela.time;

public class Clock {
	private static TimeSource timesource = new SystemClock();

	public static long now() {
		return timesource.now();
	}

	public static void freeze() {
		FakeClock fakeClock = new FakeClock(now());
		timesource = fakeClock;
	}

	public static void advance(long milliseconds) {
		timesource.advance(milliseconds);
	}

	public static void reset() {
		timesource = new SystemClock();
	}

	private static abstract class TimeSource {
		public abstract long now();

		public void advance(long milliseconds)
				throws UnsupportedOperationException {
			String msg = "Advancing time is not supported by "
					+ getClass().getName();
			throw new UnsupportedOperationException(msg);
		}
	}

	private static class SystemClock extends TimeSource {
		@Override
		public long now() {
			return System.currentTimeMillis();
		}
	}

	private static class FakeClock extends TimeSource {
		private long now;

		public FakeClock(long now) {
			this.now = now;
		}

		@Override
		public long now() {
			return now;
		}

		public void advance(long milliseconds)
				throws UnsupportedOperationException {
			now += milliseconds;
		}
	}
}
