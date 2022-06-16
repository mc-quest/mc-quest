package com.mcquest.server.character;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface DamageSource {
    public @NotNull Component getDisplayName();
}
