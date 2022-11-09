package com.mcquest.server.event;

import net.minestom.server.event.Event;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EventEmitter<E extends Event> {
    private final Set<Consumer<E>> callbacks;

    public EventEmitter() {
        callbacks = new HashSet<>();
    }

    public void emit(E event) {
        for (Consumer<E> callback : callbacks) {
            callback.accept(event);
        }
    }

    public Subscription<E> subscribe(Consumer<E> callback) {
        callbacks.add(callback);
        return new Subscription<>(this, callback);
    }

    /**
     * Called internally by Subscription.
     */
    void unsubscribe(Consumer<E> callback) {
        callbacks.remove(callback);
    }
}
