package com.lassekoskela.maven.buildevents;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class FieldMatcherTest {
	private static final String CORRECT_FIELD = "field1";
	private static final String CORRECT_VALUE = "1";
	private WhateverObject candidate;

	private static class WhateverObject {
		public WhateverObject(String value1) {
			field1 = value1;
		}

		public String field1, field2;
	}

	@Before
	public void setUp() {
		candidate = new WhateverObject(CORRECT_VALUE);
	}

	@Test
	public void matchesWhenPublicFieldHasExpectedValue() throws Exception {
		FieldMatcher<WhateverObject> m = matcher(CORRECT_FIELD, CORRECT_VALUE);
		assertThat(m.matches(candidate), is(true));
	}

	@Test
	public void doesNotMatchWhenTheFieldValueDoesNotMatch() throws Exception {
		FieldMatcher<WhateverObject> m = matcher(CORRECT_FIELD, "wrongValue");
		assertThat(m.matches(candidate), is(false));
	}

	@Test
	public void doesNotMatchWhenThereIsNoSuchField() throws Exception {
		FieldMatcher<WhateverObject> m = matcher("noSuchField", CORRECT_VALUE);
		assertThat(m.matches(candidate), is(false));
	}

	private FieldMatcher<WhateverObject> matcher(String field, String value) {
		return new FieldMatcher<WhateverObject>(field, value);
	}
}
