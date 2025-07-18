package net.mcquest.core.event;

import net.minestom.server.event.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class EventEmitter<E extends Event> {
    private final Collection<Consumer<E>> callbacks;

    public EventEmitter() {
        callbacks = new ArrayList<>();
    }

    public void emit(E event) {
        // Copy callbacks to prevent concurrent modification when a callback
        // unsubscribes.
        for (Consumer<E> callback : new ArrayList<>(callbacks)) {
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
