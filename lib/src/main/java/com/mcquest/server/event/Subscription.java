package com.mcquest.server.event;

import net.minestom.server.event.Event;

import java.util.function.Consumer;

/**
 * A subscription to an EventEmitter.
 */
public class Subscription<E extends Event> {
    private final EventEmitter<E> emitter;
    private final Consumer<E> callback;

    Subscription(EventEmitter<E> emitter, Consumer<E> callback) {
        this.emitter = emitter;
        this.callback = callback;
    }

    public void unsubscribe() {
        emitter.unsubscribe(callback);
    }
}
