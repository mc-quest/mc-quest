package net.mcquest.core.playerclass;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.audio.Sounds;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.character.PlayerCharacterManager;
import net.mcquest.core.event.ActiveSkillUseEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The PlayerClassManager is used to register and retrieve PlayerClasses.
 */
public class PlayerClassManager {
    private final Mmorpg mmorpg;
    private final Map<String, PlayerClass> playerClassesById;

    @ApiStatus.Internal
    public PlayerClassManager(Mmorpg mmorpg, PlayerClass[] playerClasses) {
        this.mmorpg = mmorpg;
        playerClassesById = new HashMap<>();
        for (PlayerClass playerClass : playerClasses) {
            registerPlayerClass(playerClass);
        }

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerChangeHeldSlotEvent.class, this::handleChangeHeldSlot);

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(this::tickSkillCooldowns).repeat(TaskSchedule.nextTick()).schedule();
    }

    private void registerPlayerClass(PlayerClass playerClass) {
        String id = playerClass.getId();
        if (playerClassesById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        playerClassesById.put(playerClass.getId(), playerClass);
    }

    private void handleChangeHeldSlot(PlayerChangeHeldSlotEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = mmorpg.getPlayerCharacterManager().getPlayerCharacter(player);
        if (pc == null) {
            return;
        }

        int slot = event.getSlot();
        if (!(slot >= SkillManager.MIN_HOTBAR_SLOT && slot <= SkillManager.MAX_HOTBAR_SLOT)) {
            return;
        }

        event.setCancelled(true);

        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getItemStack(slot);
        SkillManager skillManager = pc.getSkillManager();

        ActiveSkill skill = (ActiveSkill) skillManager.getSkill(itemStack);
        if (skill == null) {
            return;
        }

        handleUseSkill(pc, skill);
    }

    private void handleUseSkill(PlayerCharacter pc, ActiveSkill skill) {
        pc.playSound(Sounds.CLICK);

        pc.getMapViewer().close();

        double manaCost = skill.getManaCost();
        if (pc.getMana() < skill.getManaCost()) {
            pc.sendMessage(Component.text("Not enough mana", NamedTextColor.RED));
            return;
        }
        if (!skill.getCooldown(pc).isZero()) {
            pc.sendMessage(Component.text("On cooldown", NamedTextColor.RED));
            return;
        }
        ActiveSkillUseEvent event = new ActiveSkillUseEvent(pc, skill);
        skill.onUse().emit(event);
        mmorpg.getGlobalEventHandler().call(event);
        if (!event.isCancelled()) {
            pc.sendMessage(Component.text("Used ", NamedTextColor.GREEN)
                    .append(Component.text(skill.getName(), NamedTextColor.YELLOW))
                    .append(Component.text(
                            String.format(" (-%d MP)", (int) Math.round(skill.getManaCost())),
                            NamedTextColor.AQUA
                    )));
            pc.removeMana(manaCost);
            pc.getSkillManager().startCooldown(skill);
        }
    }

    /**
     * Returns the PlayerClass with the given name.
     */
    public PlayerClass getPlayerClass(String id) {
        return playerClassesById.get(id);
    }

    public Collection<PlayerClass> getPlayerClasses() {
        return Collections.unmodifiableCollection(playerClassesById.values());
    }

    private void tickSkillCooldowns() {
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        for (PlayerCharacter pc : pcManager.getPlayerCharacters()) {
            SkillManager skillManager = pc.getSkillManager();
            skillManager.tickSkillCooldowns();
        }
    }
}
