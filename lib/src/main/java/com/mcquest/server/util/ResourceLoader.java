package com.mcquest.server.util;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceLoader {
    private static Gson gson;

    public static InputStream getResourceAsStream(String resourceName) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(resourceName);
    }

    /**
     * Deserializes a JSON resource.+
     *
     * @param resourceName the name of the resource file, relative to the
     *                     resources directory
     * @param classOfT     the class of the object being deserialized
     * @param <T>          the type of the object being deserialized
     * @return the deserialized object
     */
    public static <T> T deserializeJsonResource(String resourceName, Class<T> classOfT) {
        InputStream inputStream = getResourceAsStream(resourceName);
        InputStreamReader reader = new InputStreamReader(inputStream);
        return gson.fromJson(reader, classOfT);
    }
}
