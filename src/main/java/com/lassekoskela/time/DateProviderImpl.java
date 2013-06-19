package com.lassekoskela.time;

import java.util.Date;

public class DateProviderImpl implements DateProvider {

	@Override
	public Date now() {
		return new Date(Clock.now());
	}

}
