package com.mcquest.core.character;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface DamageSource {
    @NotNull Component getDisplayName();
}
