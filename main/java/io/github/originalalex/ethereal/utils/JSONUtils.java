package io.github.originalalex.ethereal.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JSONUtils {

    private static JsonParser parser = new JsonParser();

    public static JsonObject getJson(String json) {
        return parser.parse(json).getAsJsonObject();
    }

    public static String getField(JsonObject data, String key) {
        return data.get(key).getAsString();
    }

}
