package com.mcquest.server.api.character;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface DamageSource {
    public @NotNull Component getDisplayName();
}
