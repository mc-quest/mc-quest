package com.mcquest.server.event;

import net.minestom.server.event.Event;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EventEmitter<E extends Event> {
    private Set<Consumer<E>> callbacks;

    public EventEmitter() {
        // Optimization for EventEmitters with no callbacks.
        callbacks = null;
    }

    public void emit(E event) {
        if (callbacks == null) {
            return;
        }
        for (Consumer<E> callback : callbacks) {
            callback.accept(event);
        }
    }

    public Subscription<E> subscribe(Consumer<E> callback) {
        if (callbacks == null) {
            callbacks = new HashSet<>();
        }
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
