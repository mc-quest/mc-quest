package com.mcquest.server.character;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.cartography.CardinalDirection;
import com.mcquest.server.event.PlayerCharacterReceiveItemEvent;
import com.mcquest.server.event.PlayerCharacterRemoveItemEvent;
import com.mcquest.server.item.*;
import com.mcquest.server.music.MusicManager;
import com.mcquest.server.music.Song;
import com.mcquest.server.persistence.PersistentItem;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.quest.PlayerCharacterQuestManager;
import com.mcquest.server.music.PlayerCharacterMusicPlayer;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.util.MathUtility;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;

public final class PlayerCharacter extends Character {
    private static final double[] EXPERIENCE_POINTS_PER_LEVEL = {
            250, 655, 1265, 2085, 3240, 4125, 4785, 5865, 7275,
            8205, 9365, 10715, 12085, 13455, 14810, 16135, 17415, 18635, 9775,
            20825, 22295, 23745, 25170, 26550, 27885, 30140, 32480, 34910, 37425,
            40675, 44100, 47705, 51490, 55460, 59625, 63985, 68545, 73305, 78280,
            83460, 88860, 94485, 100330, 106405, 112715, 119265, 129995, 139665, 154075,
            194030, 212870, 225770, 240255, 255180, 272795, 291495, 311490, 332165, 353410
    };
    private static final double MAX_EXPERIENCE_POINTS = Arrays.stream(EXPERIENCE_POINTS_PER_LEVEL).sum();

    private final Mmorpg mmorpg;
    private final Player player;
    private final PlayerClass playerClass;
    private final PlayerCharacterQuestManager questManager;
    private final PlayerCharacterMusicPlayer musicPlayer;
    private final Hitbox hitbox;
    private Pos respawnPosition;
    private double mana;
    private double maxMana;
    private double healthRegenRate;
    private double manaRegenRate;
    private double experiencePoints;
    private boolean isDisarmed;
    private Task undisarmTask;
    private long undisarmTime;

    PlayerCharacter(@NotNull Mmorpg mmorpg, @NotNull Player player, @NotNull PlayerCharacterData data) {
        super(Component.text(player.getUsername(), NamedTextColor.GREEN),
                levelForExperiencePoints(data.getExperiencePoints()),
                mmorpg.getInstanceManager().getInstance(data.getInstance()), data.getPosition());
        this.mmorpg = mmorpg;
        this.player = player;
        hitbox = new PlayerCharacter.Hitbox(this);
        respawnPosition = data.getRespawnPosition();
        // hidePlayerNameplate();
        playerClass = null;
        questManager = new PlayerCharacterQuestManager();
        MusicManager musicManager = mmorpg.getMusicManager();
        musicPlayer = new PlayerCharacterMusicPlayer(this);
        Integer songId = data.getSongId();
        if (songId != null) {
            Song song = musicManager.getSong(songId);
            musicPlayer.setSong(song);
        }
        setMaxHealth(data.getMaxHealth());
        setHealth(data.getHealth());
        this.maxMana = data.getMaxMana();
        this.mana = data.getMana();
        healthRegenRate = 1;
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        physicsManager.addCollider(hitbox);
        isDisarmed = false;
        undisarmTask = null;
        undisarmTime = 0;
        PersistentItem[] persistentItems = data.getItems();
        ItemManager itemManager = mmorpg.getItemManager();
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < persistentItems.length; i++) {
            PersistentItem persistentItem = persistentItems[i];
            if (persistentItem != null) {
                int itemId = persistentItem.getItemId();
                int itemAmount = persistentItem.getAmount();
                Item item = itemManager.getItem(itemId);
                ItemStack itemStack = item.getItemStack().withAmount(itemAmount);
                inventory.setItemStack(i, itemStack);
            }
        }
        initUi();
    }

    private void initUi() {
        updateActionBar();
        // Updating action bar must be delayed to work properly.
        MinecraftServer.getSchedulerManager().buildTask(this::updateExperienceBar)
                .delay(TaskSchedule.nextTick()).schedule();
    }

    @Override
    public void setInstance(Instance instance) {
        super.setInstance(instance);
        if (player.getInstance() != instance) {
            player.setInstance(instance).join();
        }
        hitbox.setInstance(instance);
    }

    @Override
    public void setPosition(@NotNull Pos position) {
        super.setPosition(position);
        hitbox.setCenter(hitboxCenter());
        if (player.getPosition().distanceSquared(position) >= 5.0) {
            player.teleport(position);
        }
        updateActionBar();
    }

    private Pos hitboxCenter() {
        return getPosition().add(0.0, 1.0, 0.0);
    }

    public Vec getLookDirection() {
        return getPosition().direction();
    }

    public Pos getEyePosition() {
        Pos position = getPosition();
        return position.withY(position.y() + 1.6);
    }

    public Pos getHandPosition() {
        Pos position = getPosition();
        Vec lookDirection = position.direction();
        return position.withY(position.y() + 1.0)
                .add(lookDirection.rotateAroundY(-Math.PI / 4.0).mul(0.5));
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public PlayerCharacterMusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        if (mana < 0.0 || mana > maxMana) {
            throw new IllegalArgumentException();
        }
        this.mana = mana;
        updateActionBar();
    }

    public void addMana(double amount) {
        if (amount < 0.0) {
            throw new IllegalArgumentException();
        }
        double newMana = MathUtility.clamp(mana + amount, 0.0, maxMana);
        setMana(newMana);
    }

    public void removeMana(double amount) {
        if (amount < 0.0) {
            throw new IllegalArgumentException();
        }
        double newMana = MathUtility.clamp(mana - amount, 0.0, maxMana);
        setMana(newMana);
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(double maxMana) {
        if (maxMana <= 0.0) {
            throw new IllegalArgumentException();
        }
        this.maxMana = maxMana;
        updateManaBar();
        updateActionBar();
    }

    public double getHealthRegenRate() {
        return healthRegenRate;
    }

    public void setHealthRegenRate(double healthRegenRate) {
        this.healthRegenRate = healthRegenRate;
    }

    public double getManaRegenRate() {
        return manaRegenRate;
    }

    public void setManaRegenRate(double manaRegenRate) {
        this.manaRegenRate = manaRegenRate;
    }

    private static int levelForExperiencePoints(double experiencePoints) {
        int level = 1;
        while (experiencePoints >= 0) {
            experiencePoints -= EXPERIENCE_POINTS_PER_LEVEL[level - 1];
            level++;
        }
        return level - 1;
    }

    public double getExperiencePoints() {
        return experiencePoints;
    }

    public void grantExperiencePoints(double experiencePoints) {
        sendMessage(Component.text("+" + (int) Math.round(experiencePoints) + " XP",
                NamedTextColor.GREEN));
        this.experiencePoints = MathUtility.clamp(this.experiencePoints + experiencePoints,
                0, MAX_EXPERIENCE_POINTS);
        checkForLevelUp();
        updateExperienceBar();
        updateActionBar();
    }

    private void checkForLevelUp() {
        int level = levelForExperiencePoints(experiencePoints);
        if (level != getLevel()) {
            levelUp();
        }
    }

    private void levelUp() {
        int newLevel = getLevel() + 1;
        super.setLevel(newLevel);
        sendMessage(Component.text("Level increased to " + newLevel, NamedTextColor.GREEN));
        grantSkillPoint();
    }

    @Override
    public void setLevel(int level) {
        // Don't set PlayerCharacter level explicitly.
        throw new UnsupportedOperationException();
    }

    private void grantSkillPoint() {
        // TODO
    }

    private void updateExperienceBar() {
        int level = getLevel();
        if (player.getLevel() != level) {
            player.setLevel(level);
        }
        double progress = experiencePointsThisLevel() / EXPERIENCE_POINTS_PER_LEVEL[level - 1];
        player.setExp((float) progress);
    }

    private double experiencePointsThisLevel() {
        int level = getLevel();
        int maxLevel = EXPERIENCE_POINTS_PER_LEVEL.length + 1;
        if (level == maxLevel) {
            return 0;
        }
        double experiencePointsThisLevel = experiencePoints;
        for (int i = 0; i < level - 1; i++) {
            experiencePointsThisLevel -= EXPERIENCE_POINTS_PER_LEVEL[i];
        }
        return experiencePointsThisLevel;
    }

    @Override
    public void setHealth(double health) {
        super.setHealth(health);
        updatePlayerHealthBar();
        updateActionBar();
    }

    @Override
    public void setMaxHealth(double maxHealth) {
        super.setMaxHealth(maxHealth);
        updatePlayerHealthBar();
        updateActionBar();
    }

    private void updatePlayerHealthBar() {
        float playerHealth = (float) (getHealth() / getMaxHealth() * 20.0);
        playerHealth = MathUtility.clamp(playerHealth, 1f, 20f);
        player.setHealth(playerHealth);
    }

    private void updateManaBar() {
        int foodLevel = (int) (getMana() / getMaxMana() * 20.0);
        player.setFood(foodLevel);
    }

    private void updateActionBar() {
        int hp = (int) Math.ceil(getHealth());
        int maxHp = (int) Math.ceil(getMaxHealth());
        int mp = (int) Math.floor(getMana());
        int maxMp = (int) Math.ceil(getMaxMana());
        Pos position = getPosition();
        int x = (int) Math.round(position.x());
        int y = (int) Math.round(position.y());
        int z = (int) Math.round(position.z());
        String direction = CardinalDirection.fromDirection(position.direction()).getAbbreviation();
        Component healthText = Component.text(String.format("%d/%d HP", hp, maxHp), NamedTextColor.RED);
        Component manaText = Component.text(String.format("%d/%d MP", mp, maxMp), NamedTextColor.AQUA);
        Component positionText = Component.text(String.format("(%d, %d, %d) %s", x, y, z, direction),
                NamedTextColor.GREEN);
        Component space = Component.text("   ");
        player.sendActionBar(healthText.append(space).append(positionText).append(space).append(manaText));
    }

    @Override
    public void damage(DamageSource source, double amount) {
        super.damage(source, amount);
        player.damage(DamageType.VOID, 0.0f);
        if (getHealth() == 0.0) {
            die();
        }
    }

    private void die() {
        setHealth(getMaxHealth());
        setPosition(respawnPosition);
        player.addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 60));
        player.showTitle(Title.title(Component.text("YOU DIED", NamedTextColor.RED),
                Component.text("Respawning...", NamedTextColor.GRAY),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))));
        player.playSound(Sound.sound(SoundEvent.ENTITY_WITHER_SPAWN, Sound.Source.MASTER, 1f, 1f));
    }

    @ApiStatus.Internal
    public PlayerCharacterQuestManager getQuestManager() {
        return questManager;
    }

    public Weapon getWeapon() {
        ItemStack itemStack = player.getInventory().getItemStack(4);
        return (Weapon) mmorpg.getItemManager().getItem(itemStack);
    }

    public ArmorItem getArmor(@NotNull ArmorSlot slot) {
        int inventorySlot = switch (slot) {
            case FEET -> 36;
            case LEGS -> 37;
            case CHEST -> 38;
            case HEAD -> 39;
        };
        ItemStack itemStack = player.getInventory().getItemStack(inventorySlot);
        return (ArmorItem) mmorpg.getItemManager().getItem(itemStack);
    }

    /**
     * Gives the item to this PlayerCharacter. Returns true if the item was
     * successfully given, false otherwise. An item is successfully given if
     * there is sufficient space in this PlayerCharacter's inventory.
     */
    public boolean giveItem(Item item) {
        return giveItem(item, 1) == 1;
    }

    public int giveItem(@NotNull Item item, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException();
        }
        ItemStack itemStack = item.getItemStack();
        PlayerInventory inventory = player.getInventory();
        int received;
        for (received = 0; received < amount; received++) {
            if (!inventory.addItemStack(itemStack)) {
                break;
            }
        }
        if (received > 0) {
            Component message = Component.text("Received ", NamedTextColor.GREEN);
            if (received != 1) {
                message = message.append(Component.text(received + " ", NamedTextColor.GREEN));
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
        ItemStack itemStack = item.getItemStack();
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
        ItemStack itemStack = item.getItemStack();
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

    public boolean isDisarmed() {
        return isDisarmed;
    }

    public void disarm(Duration duration) {
        isDisarmed = true;
        if (undisarmTask != null) {
            if (System.currentTimeMillis() + duration.toMillis() > undisarmTime) {
                undisarmTask.cancel();
            } else {
                return;
            }
        }
        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        undisarmTime = System.currentTimeMillis() + duration.toMillis();
        undisarmTask = scheduler.buildTask(() -> {
            isDisarmed = false;
            undisarmTask = null;
        }).delay(duration).schedule();
    }

    public void sendMessage(Component message) {
        player.sendMessage(message);
    }

    public void playSound(Sound sound) {
        player.playSound(sound);
    }

    @Override
    public boolean isFriendly(Character other) {
        if (other instanceof PlayerCharacter) {
            return true;
        }
        return other.isFriendly(this);
    }

    private void hidePlayerNameplate() {
        Entity passenger = new EntityCreature(EntityType.ARMOR_STAND);
        passenger.setInvisible(true);
        passenger.setInstance(getInstance());
        player.addPassenger(passenger);
    }

    public static class Hitbox extends CharacterHitbox {
        public Hitbox(PlayerCharacter pc) {
            super(pc, pc.getInstance(), pc.hitboxCenter(), 1.0, 2.0, 1.0);
        }

        @Override
        public PlayerCharacter getCharacter() {
            return (PlayerCharacter) super.getCharacter();
        }
    }
}
