package com.mcquest.server.ui;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.PlayerCharacterBasicAttackEvent;
import com.mcquest.server.event.PlayerCharacterRegisterEvent;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.item.ConsumableItem;
import com.mcquest.server.item.Item;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.item.Weapon;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.playerclass.Skill;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class InteractionHandler {
    private final Mmorpg mmorpg;

    public InteractionHandler(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
    }

    public void registerListeners() {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PickupItemEvent.class, this::handlePickupItem);
        eventHandler.addListener(PlayerCharacterRegisterEvent.class, this::handlePlayerCharacterRegister);
        eventHandler.addListener(PlayerChangeHeldSlotEvent.class, this::handleChangeHeldSlot);
        eventHandler.addListener(PlayerHandAnimationEvent.class, this::handleBasicAttack);
    }

    private void handlePickupItem(PickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            PlayerCharacter pc = PlayerCharacter.forPlayer(player);
            if (pc == null) {
                return;
            }
            PlayerInventory inventory = player.getInventory();
            boolean successful = inventory.addItemStack(event.getItemStack());
            if (!successful) {
                event.setCancelled(true);
            }
        }
    }

    private void handlePlayerCharacterRegister(PlayerCharacterRegisterEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Player player = pc.getPlayer();
        player.setHeldItemSlot((byte) 4);
    }

    private void handleChangeHeldSlot(PlayerChangeHeldSlotEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = PlayerCharacter.forPlayer(player);
        if (pc == null) {
            return;
        }
        event.setCancelled(true);
        int slot = event.getSlot();
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getItemStack(slot);
        Item item = mmorpg.getItemManager().getItem(itemStack);
        if (item instanceof ConsumableItem consumable) {
            int newAmount = itemStack.amount() - 1;
            inventory.setItemStack(slot, itemStack.withAmount(newAmount));
            pc.sendMessage(Component.text("Used ", NamedTextColor.GRAY).append(item.getDisplayName()));
            return;
        }

        Skill skill = PlayerClassManager.getSkill(itemStack);
        // TODO: Check for skills/consumables (might want to do skill checking in playerclass package)
    }

    private void handleBasicAttack(PlayerHandAnimationEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = PlayerCharacter.forPlayer(player);
        if (pc == null) {
            return;
        }
        if (pc.isDisarmed()) {
            return;
        }
        MinecraftServer.getGlobalEventHandler().call(new PlayerCharacterBasicAttackEvent(pc));
    }
}
