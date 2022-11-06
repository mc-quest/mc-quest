package com.mcquest.server.ui;

import com.mcquest.server.character.PlayerCharacter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;

import java.time.Duration;

public class Tutorial {
    public static void message(PlayerCharacter pc, Component message) {
        message = Component.empty()
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(Component.text("Tutorial", NamedTextColor.GREEN))
                .append(Component.text("]: ", NamedTextColor.GRAY))
                .append(message);
        pc.sendMessage(message);
    }

    public static void message(PlayerCharacter pc, Component message, Duration delay) {
        MinecraftServer.getSchedulerManager()
                .buildTask(() -> message(pc, message))
                .delay(delay)
                .schedule();
    }
}
