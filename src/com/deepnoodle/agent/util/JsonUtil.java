package com.deepnoodle.agent.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {

	private static final String ENCODED_DOUBLE_QUOTE = "&#034;";
	public static final String DATE_FORMAT_ISO6801 = "yyyy-MM-dd'T'HH:mm:ssZ";

	private static GsonBuilder getGsonBuilder(boolean serializeNulls, String dateFormat,
			boolean sanitizeStrings) {
		GsonBuilder gsonBuilder = new GsonBuilder()
				.setExclusionStrategies(new GsonAnnotationExclusionStrategy());
		gsonBuilder.setPrettyPrinting();
		if (dateFormat != null) {
			gsonBuilder.setDateFormat(dateFormat);
		}

		if (serializeNulls) {
			gsonBuilder.serializeNulls();
		}
		if (sanitizeStrings) {
			//gsonBuilder.registerTypeHierarchyAdapter(CharSequence.class, new JsonStringDeserializer());
		}
		return gsonBuilder;
	}

	private static Gson getGson(boolean serializeNulls, String dateFormat, boolean sanitizeStrings) {
		return getGsonBuilder(serializeNulls, dateFormat, sanitizeStrings).create();
	}

	public static <T> T fromJson(String json, Type type) {
		return fromJson(json, type, true, null);
	}

	public static <T> T fromJson(String json, Type type, boolean sanitizeStrings, String dateFormat) {
		return fromJson(json, type, true, sanitizeStrings, dateFormat);
	}

	public static <T> T fromJson(String json, Type type, boolean serializeNulls, boolean sanitizeStrings,
			String dateFormat) {
		if (sanitizeStrings) {
			json = json.replace(ENCODED_DOUBLE_QUOTE, "\"");
		}
		return getGson(true, dateFormat, sanitizeStrings).fromJson(json, type);
	}

	public static String toJson(Object object) {
		return toJson(object, false);
	}

	public static String toJson(Object object, boolean serializeNulls) {

		String json = getGson(serializeNulls, DATE_FORMAT_ISO6801, false).toJson(object);
		//		ObjectMapper mapper = JsonFactory.create();
		//		String json = mapper.writeValueAsString(object);
		return json;
	}

}
