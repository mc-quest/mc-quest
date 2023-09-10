package com.mcquest.core.ai;

import java.util.HashMap;

public class Blackboard {
    private final HashMap<String, Object> map;

    Blackboard() {
        map = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(BlackboardKey<T> key) {
        return (T) map.get(key.getKey());
    }

    public <T> void set(BlackboardKey<T> key, T value) {
        map.put(key.getKey(), value);
    }

    public <T> void remove(BlackboardKey<T> key) {
        map.remove(key.getKey());
    }
}
