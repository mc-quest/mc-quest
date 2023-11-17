package net.mcquest.core.stat.modifier;

import net.mcquest.core.stat.CharacterStats;

public interface CharacterStatModifier {
    public void activate(CharacterStats stats);

    public void deactivate(CharacterStats stats);
}
