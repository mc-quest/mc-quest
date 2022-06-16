package com.mcquest.server.event;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;

import java.util.function.Consumer;

public class EventManager {
    public static void callEvent(Event event) {
        MinecraftServer.getGlobalEventHandler().call(event);
    }

    public static <E extends Event> void addListener(Class<E> eventType, Consumer<E> listener) {
        MinecraftServer.getGlobalEventHandler().addListener(eventType, listener);
    }
}
