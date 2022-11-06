package com.mcquest.server.event;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Event<T> {
    private final Set<Consumer<T>> callbacks;

    public Event() {
        callbacks = new HashSet<>();
    }

    public void invoke(T t) {
        for (Consumer<T> callback : callbacks) {
            callback.accept(t);
        }
    }

    public void subscribe(Consumer<T> callback) {
        callbacks.add(callback);
    }

    public void unsubscribe(Consumer<T> callback) {
        callbacks.remove(callback);
    }
}
