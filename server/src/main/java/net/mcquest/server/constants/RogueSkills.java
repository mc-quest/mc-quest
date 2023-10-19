package net.mcquest.server.constants;

import net.mcquest.core.playerclass.ActiveSkill;
import net.mcquest.core.playerclass.PassiveSkill;

public class RogueSkills {
    public static final ActiveSkill DASH = (ActiveSkill) PlayerClasses.ROGUE.getSkill(1);
    public static final ActiveSkill BACKSTAB = (ActiveSkill) PlayerClasses.ROGUE.getSkill(2);
    public static final ActiveSkill SNEAK = (ActiveSkill) PlayerClasses.ROGUE.getSkill(3);
    public static final ActiveSkill ADRENALINE = (ActiveSkill) PlayerClasses.ROGUE.getSkill(4);

    public static final PassiveSkill FLEETOFFOOT = (PassiveSkill) PlayerClasses.ROGUE.getSkill(5);

}
