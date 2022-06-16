package com.mcquest.server.character;

import com.mcquest.server.event.PlayerCharacterReceiveItemEvent;
import com.mcquest.server.event.PlayerCharacterRemoveItemEvent;
import com.mcquest.server.item.ArmorItem;
import com.mcquest.server.item.Item;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.item.Weapon;
import com.mcquest.server.physics.Collider;
import com.mcquest.server.quest.PlayerCharacterQuestManager;
import com.mcquest.server.sound.PlayerCharacterMusicManager;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.util.Debug;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class PlayerCharacter extends Character {
    private static final Map<Player, PlayerCharacter> playersMap = new HashMap<>();

    private final Player player;
    private final PlayerClass playerClass;
    private final PlayerCharacterQuestManager questManager;
    private final PlayerCharacterMusicManager musicManager;
    private final Hitbox hitbox;
    private double mana;
    private double maxMana;
    private double healthRegenRate;
    private double manaRegenRate;
    private double experiencePoints;

    static {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerMoveEvent.class, event -> {
            Player player = event.getPlayer();
            PlayerCharacter pc = forPlayer(player);
            if (pc != null) {
                pc.setPosition(event.getNewPosition());
            }
        });
    }

    private PlayerCharacter(Player player, Instance instance, Pos position) {
        super(Component.text(player.getUsername(), NamedTextColor.GREEN), 1,
                instance, position);
        this.player = player;
        hitbox = new PlayerCharacter.Hitbox(this);
        setInstance(instance);
        setPosition(position);
        hidePlayerNameplate();
        playerClass = null;
        questManager = new PlayerCharacterQuestManager();
        musicManager = new PlayerCharacterMusicManager(this);
        hitbox.setEnabled(true);
    }

    public static PlayerCharacter register(Player player, PlayerCharacterData data) {
        Instance instance = (Instance) MinecraftServer.getInstanceManager().getInstances().toArray()[1];
        PlayerCharacter pc = new PlayerCharacter(player, instance, player.getPosition());
        playersMap.put(player, pc);
        return pc;
    }

    @Override
    public void setInstance(Instance instance) {
        super.setInstance(instance);
        if (player.getInstance() != instance) {
            player.setInstance(instance);
        }
        hitbox.setInstance(instance);
    }

    @Override
    public void setPosition(Pos position) {
        super.setPosition(position);
        hitbox.setCenter(position.add(0.0, 1.0, 0.0));
        if (player.getPosition().distanceSquared(position) >= 5.0) {
            player.teleport(position);
        }
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

    @Override
    public void damage(DamageSource source, double amount) {
        super.damage(source, amount);
        player.damage(DamageType.VOID, 0.0f);
    }

    @ApiStatus.Internal
    public PlayerCharacterQuestManager getQuestManager() {
        return questManager;
    }

    public Weapon getWeapon() {
        ItemStack itemStack = player.getInventory().getItemStack(4);
        return (Weapon) ItemManager.getItem(itemStack);
    }

    public ArmorItem getArmor(@NotNull ArmorItem.Slot slot) {
        int inventorySlot = switch (slot) {
            case FEET -> 36;
            case LEGS -> 37;
            case CHEST -> 38;
            case HEAD -> 39;
        };
        ItemStack itemStack = player.getInventory().getItemStack(inventorySlot);
        return (ArmorItem) ItemManager.getItem(itemStack);
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
        int received;
        for (received = 0; received < amount; received++) {
            if (!inventory.addItemStack(itemStack)) {
                break;
            }
        }
        if (received > 0) {
            Component message = Component.text("Received ");
            if (received != 1) {
                message = message.append(Component.text(received + " "));
            }
            message = message.append((item.getDisplayName()));
            sendMessage(message);
            PlayerCharacterReceiveItemEvent event = new
                    PlayerCharacterReceiveItemEvent(this, item, received);
            MinecraftServer.getGlobalEventHandler().call(event);
        }
        return received;
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
            MinecraftServer.getGlobalEventHandler().call(event);
        }
        return amountRemoved;
    }

    public void sendMessage(Component message) {
        player.sendMessage(message);
    }

    @Override
    public boolean isFriendly(Character other) {
        if (other instanceof PlayerCharacter) {
            return true;
        }
        return other.isFriendly(this);
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
            if (contact instanceof PlayerCharacter.Hitbox hitbox) {
                PlayerCharacter pc = hitbox.getCharacter();
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

    private void hidePlayerNameplate() {
        Entity passenger = new EntityCreature(EntityType.ARMOR_STAND);
        passenger.setInvisible(true);
        passenger.setInstance(getInstance());
        player.addPassenger(passenger);
    }

    public void remove() {
        hitbox.setEnabled(false);
        playersMap.remove(player);
    }

    public static class Hitbox extends CharacterHitbox {
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
