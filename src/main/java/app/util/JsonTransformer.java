package app.util;

import com.google.gson.Gson;
import spark.ResponseTransformer;

import java.lang.reflect.Type;

public class JsonTransformer {
	public static String toJson(Object object) {
		return new Gson().toJson(object);
	}

	public static ResponseTransformer json() {
		return JsonTransformer::toJson;
	}

	public static <T> T fromJson(String object, Type type) {
		return new Gson().fromJson(object, type);
	}
}
