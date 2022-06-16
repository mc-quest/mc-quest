package com.mcquest.server.api.character;

import com.mcquest.server.api.event.EventManager;
import com.mcquest.server.api.event.PlayerCharacterReceiveItemEvent;
import com.mcquest.server.api.event.PlayerCharacterRemoveItemEvent;
import com.mcquest.server.api.item.Item;
import com.mcquest.server.api.persistence.PlayerCharacterData;
import com.mcquest.server.api.physics.Collider;
import com.mcquest.server.api.playerclass.PlayerClass;
import com.mcquest.server.api.quest.PlayerCharacterQuestManager;
import com.mcquest.server.api.sound.PlayerCharacterMusicManager;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public final class PlayerCharacter extends Character {
    private static Map<Player, PlayerCharacter> playersMap = new HashMap<>();

    private final Player player;
    private final PlayerClass playerClass;
    private final PlayerCharacterQuestManager questManager;
    private PlayerCharacterMusicManager musicManager;
    private double mana;
    private double maxMana;
    private double healthRegenRate;
    private double manaRegenRate;
    private double experiencePoints;

    static {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerMoveEvent.class, playerMoveEvent -> {
            Player player = playerMoveEvent.getPlayer();
            PlayerCharacter pc = forPlayer(player);
            if (pc != null) {
                pc.setPosition(playerMoveEvent.getNewPosition());
            }
        });
    }

    private PlayerCharacter(Player player, Instance instance, Pos position) {
        super(player.getDisplayName(), 1, instance, position);
        this.player = player;
        playerClass = null;
        questManager = new PlayerCharacterQuestManager();
    }

    public static PlayerCharacter registerPlayerCharacter(Player player, PlayerCharacterData data) {
        PlayerCharacter pc = new PlayerCharacter(player, null, null);
        playersMap.put(player, pc);
        return pc;
    }

    public static PlayerCharacter forPlayer(Player player) {
        return playersMap.get(player);
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public PlayerCharacterMusicManager getMusicManager() {
        return musicManager;
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        if (mana < 0.0 || mana > maxMana) {
            throw new IllegalArgumentException();
        }
        this.mana = mana;
    }

    public double getMaxMana() {
        return maxMana;
    }

    private double getExperiencePoints() {
        return experiencePoints;
    }

    public void grantExperiencePoints(double experiencePoints) {
        // TODO
    }

    @ApiStatus.Internal
    public PlayerCharacterQuestManager getQuestManager() {
        return questManager;
    }

    /**
     * Gives the item to this PlayerCharacter. Returns true if the item was
     * successfully given, false otherwise. An item is successfully given if
     * there is sufficient space in this PlayerCharacter's inventory.
     */
    public boolean giveItem(Item item) {
        return giveItem(item, 1) == 1;
    }

    public int giveItem(Item item, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException();
        }
        ItemStack itemStack = Objects.requireNonNull(item).getItemStack();
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < amount; i++) {
            if (!inventory.addItemStack(itemStack)) {
                return i;
            }
        }
        if (amount > 0) {
            Component message = Component.text("Received ");
            if (amount != 1) {
                message = message.append(Component.text(amount + " "));
            }
            message = message.append((item.getDisplayName()));
            sendMessage(message);
            PlayerCharacterReceiveItemEvent event = new
                    PlayerCharacterReceiveItemEvent(this, item, amount);
            EventManager.callEvent(event);
        }
        return amount;
    }

    /**
     * Returns how much of the item is in this PlayerCharacter's inventory.
     * Weapons and armor items that are currently equipped are not counted.
     * Items on the PlayerCharacter's cursor are also not counted.
     */
    public int getItemCount(Item item) {
        ItemStack itemStack = Objects.requireNonNull(item).getItemStack();
        PlayerInventory inventory = player.getInventory();
        int count = 0;
        for (int i = 0; i < 36; i++) {
            if (i == 4) {
                // TODO: Make sure 4 is correct weapon slot.
                // Skip weapon.
                continue;
            }
            ItemStack inventoryItemStack = inventory.getItemStack(i);
            if (inventoryItemStack.isSimilar(itemStack)) {
                count += inventoryItemStack.amount();
            }
        }
        return count;
    }

    private boolean removeItem(Item item) {
        return removeItem(item, 1) == 1;
    }

    private int removeItem(Item item, int amount) {
        ItemStack itemStack = Objects.requireNonNull(item).getItemStack();
        if (amount < 0) {
            throw new IllegalArgumentException();
        }
        PlayerInventory inventory = player.getInventory();
        int amountRemoved = 0;
        for (int i = 0; i < 36; i++) {
            if (i == 4) {
                // Don't remove weapon.
                continue;
            }
            ItemStack inventoryItemStack = inventory.getItemStack(i);
            if (inventoryItemStack.isSimilar(itemStack)) {
                int reduction = Math.min(itemStack.amount(), amount);
                ItemStack newItemStack = inventoryItemStack.withAmount(
                        itemStack.amount() - reduction);
                amount -= reduction;
                amountRemoved += reduction;
                inventory.setItemStack(i, newItemStack);
                if (amount == 0) {
                    break;
                }
            }
        }
        if (amountRemoved > 0) {
            Component message = Component.text("Removed ");
            if (amountRemoved != 1) {
                message = message.append(Component.text(amountRemoved + " "));
            }
            message = message.append((item.getDisplayName()));
            sendMessage(message);
            PlayerCharacterRemoveItemEvent event = new
                    PlayerCharacterRemoveItemEvent(this, item, amountRemoved);
            EventManager.callEvent(event);
        }
        return amountRemoved;
    }

    public void sendMessage(Component message) {
        player.sendMessage(message);
    }

    public static Set<PlayerCharacter> getNearby(Instance instance,
                                                 Pos position, double radius) {
        Set<PlayerCharacter> nearby = new HashSet<>();
        Collider collider = new Collider(instance, position, radius, radius,
                radius);
        collider.setEnabled(true);
        Set<Collider> contacts = collider.getContacts();
        double radiusSquared = radius * radius;
        for (Collider contact : contacts) {
            if (contact instanceof Hitbox pcCollider) {
                PlayerCharacter pc = pcCollider.getCharacter();
                if (pc.getPosition().distanceSquared(position) <= radiusSquared) {
                    nearby.add(pc);
                }
            }
        }
        collider.setEnabled(false);
        return nearby;
    }

    /**
     * Returns true if there is a PlayerCharacter within the given radius of
     * the given position in the given instance. Returns false otherwise.
     */
    public static boolean isNearby(Instance instance, Pos position,
                                   double radius) {
        return !getNearby(instance, position, radius).isEmpty();
    }

    public static class Hitbox extends CharacterCollider {
        public Hitbox(PlayerCharacter pc) {
            super(pc, pc.getInstance(), pc.getPosition().add(0.0, 1.0, 0.0),
                    1.0, 2.0, 1.0);
        }

        @Override
        public PlayerCharacter getCharacter() {
            return (PlayerCharacter) super.getCharacter();
        }
    }
}
