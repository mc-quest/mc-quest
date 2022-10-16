package com.mcquest.server.playerclass;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.item.Material;

import java.time.Duration;

public class ActiveSkill extends Skill {
    private final double manaCost;

    ActiveSkill(int id, String name, int level, Material icon, String description,
                int skillTreeRow, int skillTreeColumn, double manaCost) {
        super(id, name, level, icon, description, skillTreeRow, skillTreeColumn);
        this.manaCost = manaCost;
    }

    public double getManaCost() {
        return manaCost;
    }

    public Duration getCooldown(PlayerCharacter pc) {
        return pc.getSkillManager().getCooldown(this);
    }
}
