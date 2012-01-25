package com.lassekoskela.maven.buildevents;

import java.lang.reflect.Field;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class FieldMatcher<T> extends BaseMatcher<T> {
	private final String field;
	private final Object value;

	public FieldMatcher(String field, Object value) {
		this.field = field;
		this.value = value;
	}

	@Override
	public boolean matches(Object candidate) {
		try {
			Field fieldObj = candidate.getClass().getField(field);
			Object actualValue = fieldObj.get(candidate);
			return areEqual(value, actualValue);
		} catch (NoSuchFieldException e) {
			return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean areEqual(Object a, Object b) {
		if (a == null) {
			return b == null;
		}
		return a.equals(b);
	}

	@Override
	public void describeTo(Description d) {
		d.appendText(field + "=").appendValue(value);
	}
}