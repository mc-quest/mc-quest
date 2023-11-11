package net.mcquest.server.constants;

import net.mcquest.core.playerclass.ActiveSkill;
import net.mcquest.core.playerclass.PassiveSkill;

public class FighterSkills {
    public static final ActiveSkill BASH = (ActiveSkill) PlayerClasses.FIGHTER.getSkill("bash");
    public static final ActiveSkill SELF_HEAL = (ActiveSkill) PlayerClasses.FIGHTER.getSkill("self_heal");
    public static final ActiveSkill OVERHEAD_STRIKE = (ActiveSkill) PlayerClasses.FIGHTER.getSkill("overhead_strike");
    public static final ActiveSkill TAUNT = (ActiveSkill) PlayerClasses.FIGHTER.getSkill("taunt");
    public static final ActiveSkill BERSERK = (ActiveSkill) PlayerClasses.FIGHTER.getSkill("berserk");
    public static final ActiveSkill WHIRLWIND = (ActiveSkill) PlayerClasses.FIGHTER.getSkill("whirlwind");
    public static final ActiveSkill CHARGE = (ActiveSkill) PlayerClasses.FIGHTER.getSkill("charge");

    public static final PassiveSkill ENLARGED_OVERHEAD_STRIKE = (PassiveSkill) PlayerClasses.FIGHTER.getSkill("enlarged_overhead_strike");
    public static final PassiveSkill ENPOWERED_OVERHEAD_STRIKE = (PassiveSkill) PlayerClasses.FIGHTER.getSkill("enpowered_overhead_strike");
}
