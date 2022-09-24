package com.mcquest.server.playerclass;

import net.minestom.server.item.Material;

public class PassiveSkill extends Skill {
    PassiveSkill(int id, String name, int level, Material icon, String description,
                 int skillTreeRow, int skillTreeColumn) {
        super(id, name, level, icon, description, skillTreeRow, skillTreeColumn);
    }
}
