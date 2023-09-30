package com.mcquest.core.character;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.asset.Asset;
import com.mcquest.core.cartography.CardinalDirection;
import com.mcquest.core.cartography.MapViewer;
import com.mcquest.core.cinema.CutscenePlayer;
import com.mcquest.core.commerce.Money;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.item.PlayerCharacterInventory;
import com.mcquest.core.mount.Mount;
import com.mcquest.core.music.MusicPlayer;
import com.mcquest.core.object.ObjectSpawner;
import com.mcquest.core.persistence.PlayerCharacterData;
import com.mcquest.core.playerclass.PlayerClass;
import com.mcquest.core.playerclass.SkillManager;
import com.mcquest.core.quest.QuestTracker;
import com.mcquest.core.util.MathUtility;
import com.mcquest.core.zone.Zone;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;

public final class PlayerCharacter extends Character {
    private static final double[] EXPERIENCE_POINTS_PER_LEVEL = Asset.of(
            PlayerCharacter.class.getClassLoader(),
            "data/experience_points_per_level.json"
    ).readJson(double[].class);
    private static final double MAX_EXPERIENCE_POINTS =
            Arrays.stream(EXPERIENCE_POINTS_PER_LEVEL).sum();

    private final Player player;
    private final PlayerClass playerClass;
    private final SkillManager skillManager;
    private final PlayerCharacterInventory inventory;
    private final QuestTracker questTracker;
    private final MusicPlayer musicPlayer;
    private final MapViewer mapViewer;
    private final CutscenePlayer cutscenePlayer;
    private Zone zone;
    private Instance respawnInstance;
    private Pos respawnPosition;
    private double mana;
    private double maxMana;
    private double healthRegenRate;
    private double manaRegenRate;
    private double experiencePoints;
    private Money money;
    private boolean canMount;
    private boolean canAct;
    private boolean isDisarmed;
    private Task undisarmTask;
    private long undisarmTime;
    private boolean teleporting;

    PlayerCharacter(Mmorpg mmorpg, ObjectSpawner spawner, Player player, PlayerCharacterData data) {
        super(mmorpg, spawner);
        this.player = player;
        setName(player.getUsername());
        playerClass = mmorpg.getPlayerClassManager().getPlayerClass(data.getPlayerClassId());
        skillManager = new SkillManager(this, data);
        inventory = new PlayerCharacterInventory(this, data, mmorpg.getItemManager());
        questTracker = new QuestTracker(this, data, mmorpg.getQuestManager());
        musicPlayer = new MusicPlayer(this, data, mmorpg.getMusicManager());
        mapViewer = new MapViewer(this, data, mmorpg.getMapManager());
        cutscenePlayer = new CutscenePlayer(this);
        setLevel(levelForExperiencePoints(data.getExperiencePoints()));
        setMaxHealth(data.getMaxHealth());
        setHealth(data.getHealth());
        maxMana = data.getMaxMana();
        mana = data.getMana();
        healthRegenRate = data.getHealthRegenRate();
        manaRegenRate = data.getManaRegenRate();
        zone = mmorpg.getZoneManager().getZone(data.getZoneId());
        respawnInstance = mmorpg.getInstanceManager().getInstance(data.getRespawnInstanceId());
        respawnPosition = data.getRespawnPosition();
        isDisarmed = false;
        undisarmTask = null;
        undisarmTime = 0;
        canMount = data.canMount();
        // TODO
        canAct = true;
        teleporting = false;
        money = new Money(data.getMoney());

        zone.addPlayerCharacter(this);

        initUi();
        hidePlayerNameplates();
        updateAttackSpeed();
    }

    private void initUi() {
        updateActionBar();
        // Updating experience bar must be delayed to work properly.
        MinecraftServer.getSchedulerManager().buildTask(this::updateExperienceBar)
                .delay(TaskSchedule.nextTick()).schedule();
    }

    private void hidePlayerNameplates() {
        Team team = new TeamBuilder("", MinecraftServer.getTeamManager())
                .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
                .build();
        player.setTeam(team);
    }

    private void updateAttackSpeed() {
        double attackSpeed = inventory.getWeapon().getAttackSpeed();
        player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue((float) attackSpeed);
    }

    @Override
    public void setInstance(@NotNull Instance instance, Pos position) {
        super.setInstance(instance, position);

        beginTeleport();
        if (player.getInstance() == instance) {
            player.teleport(position).thenRun(this::completeTeleport);
        } else {
            player.setInstance(instance, position).thenRun(this::completeTeleport);
        }

        getHitbox().setInstance(instance);
    }

    @Override
    void updatePosition(@NotNull Pos position) {
        super.updatePosition(position);
        updateActionBar();
    }

    @Override
    public void setPosition(@NotNull Pos position) {
        beginTeleport();
        player.teleport(position).thenRun(this::completeTeleport);
        updatePosition(position);
    }

    boolean isTeleporting() {
        return teleporting;
    }

    private void beginTeleport() {
        teleporting = true;
        player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.0f);
        player.addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, Integer.MAX_VALUE));
    }

    private void completeTeleport() {
        teleporting = false;
        player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1f);
        player.removeEffect(PotionEffect.BLINDNESS);
    }

    public Instance getRespawnInstance() {
        return respawnInstance;
    }

    public Pos getRespawnPosition() {
        return respawnPosition;
    }

    public void setRespawnPoint(@NotNull Instance instance, @NotNull Pos position) {
        this.respawnInstance = instance;
        this.respawnPosition = position;
    }

    private Pos hitboxCenter() {
        return getPosition().withY(y -> y + 0.9);
    }

    public Pos getEyePosition() {
        Pos position = getPosition();
        return position.withY(position.y() + player.getEyeHeight());
    }

    public Pos getHandPosition() {
        Pos position = getPosition();
        Vec lookDirection = position.direction();
        return position.withY(y -> y + (player.isSneaking() ? 0.65 : 1.0))
                .add(lookDirection.rotateAroundY(-Math.PI / 4.0).mul(0.5));
    }

    public Pos getWeaponPosition() {
        return getHandPosition().withY(y -> y + 0.5);
    }

    public Pos getTargetBlockPosition(double maxDistance) {
        Point target = player.getTargetBlockPosition((int) maxDistance);
        if (target == null) {
            return null;
        }
        return Pos.fromPoint(target);
    }

    public Vec getLookDirection() {
        return getPosition().direction();
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public MapViewer getMapViewer() {
        return mapViewer;
    }

    public CutscenePlayer getCutscenePlayer() {
        return cutscenePlayer;
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        if (mana < 0.0 || mana > maxMana) {
            throw new IllegalArgumentException();
        }
        this.mana = mana;
        updateManaBar();
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
        if (experiencePoints == 0.0) {
            return;
        }

        sendMessage(Component.text("+" + (int) Math.round(experiencePoints) + " XP",
                NamedTextColor.GREEN));
        this.experiencePoints = MathUtility.clamp(this.experiencePoints + experiencePoints,
                0, MAX_EXPERIENCE_POINTS);
        checkForLevelUp();
        updateExperienceBar();
    }

    private void checkForLevelUp() {
        int newLevel = levelForExperiencePoints(experiencePoints);
        while (newLevel > getLevel()) {
            levelUp();
        }
    }

    private void levelUp() {
        int newLevel = getLevel() + 1;
        setLevel(newLevel);
        sendMessage(Component.text("Level increased to " + newLevel + "!", NamedTextColor.GREEN));
        skillManager.grantSkillPoint();
        sendMessage(Component.text("Received 1 skill point!", NamedTextColor.GREEN));
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
    public void damage(@NotNull DamageSource source, double amount) {
        super.damage(source, amount);
        player.damage(DamageType.VOID, 0.0f);
        if (getHealth() == 0.0) {
            die();
        }
    }

    private void die() {
        setInstance(respawnInstance, respawnPosition);
        setHealth(getMaxHealth());
        setMana(maxMana);

        player.addEffect(new Potion(PotionEffect.BLINDNESS, (byte) 1, 60));

        player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.0f);
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1f);
        }).delay(TaskSchedule.millis(2500)).schedule();

        player.showTitle(Title.title(Component.text("YOU DIED", NamedTextColor.RED),
                Component.text("Respawning...", NamedTextColor.GRAY),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))));

        player.playSound(Sound.sound(SoundEvent.ENTITY_WITHER_SPAWN, Sound.Source.MASTER, 1f, 1f));
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public PlayerCharacterInventory getInventory() {
        return inventory;
    }

    public QuestTracker getQuestTracker() {
        return questTracker;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(@NotNull Zone zone) {
        if (this.zone == zone) {
            return;
        }

        this.zone.removePlayerCharacter(this);
        this.zone = zone;
        zone.addPlayerCharacter(this);
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    /**
     * Returns the mount currently being ridden, or null if there is none.
     */
    public @Nullable Mount getMount() {
        // TODO
        // may want to return a MountInstance instead?
        return null;
    }

    public boolean canMount() {
        return canMount;
    }

    public void setCanMount(boolean canMount) {
        this.canMount = canMount;
        // TODO: unmount
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
        undisarmTime = System.currentTimeMillis() + duration.toMillis();
        undisarmTask = MinecraftServer.getSchedulerManager().buildTask(() -> {
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

    public void stopSound(SoundStop stop) {
        player.stopSound(stop);
    }

    @Override
    public Attitude getAttitude(@NotNull Character other) {
        if (other instanceof PlayerCharacter) {
            return Attitude.FRIENDLY;
        }
        return other.getAttitude(this);
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return !(source instanceof PlayerCharacter);
    }

    public boolean canAct() {
        return canAct;
    }

    public void setCanAct(boolean canAct) {
        this.canAct = canAct;
        // TODO: disable skills, consumables, and basic attacks
    }
}
