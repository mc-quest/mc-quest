package net.mcquest.server.constants;

import net.mcquest.core.playerclass.ActiveSkill;
import net.mcquest.core.playerclass.PassiveSkill;

public class RogueSkills {
    public static final ActiveSkill DASH = (ActiveSkill) PlayerClasses.ROGUE.getSkill("dash");
    public static final ActiveSkill BACKSTAB = (ActiveSkill) PlayerClasses.ROGUE.getSkill("backstab");
    public static final ActiveSkill SNEAK = (ActiveSkill) PlayerClasses.ROGUE.getSkill("sneak");
    //public static final ActiveSkill ADRENALINE = (ActiveSkill) PlayerClasses.ROGUE.getSkill("adrenaline");
    public static final ActiveSkill FANOFKNIVES = (ActiveSkill) PlayerClasses.ROGUE.getSkill("fanofknives");
}
