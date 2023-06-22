package com.mcquest.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStreamReader;

public class JsonUtility {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static String stringify(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T parse(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T parse(InputStreamReader reader, Class<T> classOfT) {
        return GSON.fromJson(reader, classOfT);
    }
}
