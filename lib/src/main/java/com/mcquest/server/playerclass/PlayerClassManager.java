package com.mcquest.server.playerclass;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.PlayerCharacterUseActiveSkillEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The PlayerClassManager is used to register and retrieve PlayerClasses.
 */
public class PlayerClassManager {
    static final Tag<Integer> PLAYER_CLASS_ID_TAG = Tag.Integer("player_class_id");
    static final Tag<Integer> SKILL_ID_TAG = Tag.Integer("skill_id");

    private final Mmorpg mmorpg;
    private final Map<Integer, PlayerClass> playerClassesById;

    @ApiStatus.Internal
    public PlayerClassManager(Mmorpg mmorpg, PlayerClass[] playerClasses) {
        this.mmorpg = mmorpg;
        playerClassesById = new HashMap<>();
        for (PlayerClass playerClass : playerClasses) {
            registerPlayerClass(playerClass);
        }
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerChangeHeldSlotEvent.class, this::handleChangeHeldSlot);
    }

    private void registerPlayerClass(PlayerClass playerClass) {
        int id = playerClass.getId();
        if (playerClassesById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        playerClassesById.put(playerClass.getId(), playerClass);
    }

    private void handleChangeHeldSlot(PlayerChangeHeldSlotEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        int slot = event.getSlot();
        ItemStack itemStack = inventory.getItemStack(slot);
        if (!itemStack.hasTag(PLAYER_CLASS_ID_TAG)) {
            return;
        }
        PlayerCharacter pc = mmorpg.getPlayerCharacterManager().getPlayerCharacter(player);
        int playerClassId = itemStack.getTag(PLAYER_CLASS_ID_TAG);
        int skillId = itemStack.getTag(SKILL_ID_TAG);
        PlayerClass playerClass = getPlayerClass(playerClassId);
        ActiveSkill skill = (ActiveSkill) playerClass.getSkill(skillId);
        handleUseSkill(pc, skill);
    }

    private void handleUseSkill(PlayerCharacter pc, ActiveSkill skill) {
        double manaCost = skill.getManaCost();
        if (pc.getMana() < skill.getManaCost()) {
            pc.sendMessage(Component.text("Not enough mana", NamedTextColor.RED));
            // TODO: sound
            return;
        }
        if (!skill.getCooldown(pc).isZero()) {
            pc.sendMessage(Component.text("On cooldown", NamedTextColor.RED));
            // TODO: sound
            return;
        }
        PlayerCharacterSkillManager skillManager = pc.getSkillManager();
        pc.removeMana(manaCost);
        PlayerCharacterUseActiveSkillEvent event = new PlayerCharacterUseActiveSkillEvent(pc, skill);
        mmorpg.getGlobalEventHandler().call(event);
    }

    /**
     * Returns the PlayerClass with the given name.
     */
    public PlayerClass getPlayerClass(int id) {
        return playerClassesById.get(id);
    }

    public Collection<PlayerClass> getPlayerClasses() {
        return Collections.unmodifiableCollection(playerClassesById.values());
    }

    public Skill getSkill(ItemStack hotbarItemStack) {
        if (!hotbarItemStack.hasTag(PLAYER_CLASS_ID_TAG) ||
                !hotbarItemStack.hasTag(SKILL_ID_TAG)) {
            return null;
        }
        int playerClassId = hotbarItemStack.getTag(PLAYER_CLASS_ID_TAG);
        PlayerClass playerClass = getPlayerClass(playerClassId);
        if (playerClass == null) {
            return null;
        }
        int skillId = hotbarItemStack.getTag(SKILL_ID_TAG);
        return playerClass.getSkill(skillId);
    }
}
