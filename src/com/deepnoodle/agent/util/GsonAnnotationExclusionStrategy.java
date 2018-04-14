package com.deepnoodle.agent.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonAnnotationExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return clazz.isAnnotationPresent(Ignorable.class);
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return (f.getAnnotation(Ignorable.class) != null);
	}
}
