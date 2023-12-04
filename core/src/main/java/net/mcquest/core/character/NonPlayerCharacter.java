package net.mcquest.core.character;

import net.kyori.adventure.text.Component;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.Behavior;
import net.mcquest.core.ai.BehaviorTree;
import net.mcquest.core.ai.Navigator;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.loot.LootTable;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.quest.QuestObjective;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.minestom.ModelEntity;

import java.time.Duration;
import java.util.*;

public class NonPlayerCharacter extends Character {
    private final EntityCreature entity;
    private final Navigator navigator;
    private final BehaviorTree brain;
    private final Set<PlayerCharacter> attackers;
    private final Collection<QuestObjective> slayQuestObjectives;
    private double experiencePoints;
    private LootTable lootTable;
    private Duration respawnDuration;
    private Character target;
    private Task endDamageTint;

    public NonPlayerCharacter(Mmorpg mmorpg, ObjectSpawner spawner, CharacterModel model) {
        super(mmorpg, spawner);
        entity = model.createEntity(this);
        navigator = new Navigator(this);
        brain = new BehaviorTree(this);
        attackers = new HashSet<>();
        experiencePoints = 0.0;
        lootTable = null;
        slayQuestObjectives = new ArrayList<>();
        target = null;
    }

    public final void setLevel(int level) {
        super.setLevel(level);
    }

    public double getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(double experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public void setLootTable(LootTable lootTable) {
        this.lootTable = lootTable;
    }

    public Collection<QuestObjective> getSlayQuestObjectives() {
        return Collections.unmodifiableCollection(slayQuestObjectives);
    }

    public void addSlayQuestObjective(QuestObjective objective) {
        slayQuestObjectives.add(objective);
    }

    public @Nullable Character getTarget() {
        return target;
    }

    public void setTarget(@Nullable Character target) {
        this.target = target;
    }

    @Override
    public EntityCreature getEntity() {
        return entity;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public final void setBrain(Behavior root) {
        brain.setRoot(root);
    }

    public Duration getRemovalDelay() {
        return Duration.ofMillis(entity.getRemovalAnimationDelay());
    }

    public final void setRemovalDelay(Duration removalDelay) {
        entity.setRemovalAnimationDelay((int) removalDelay.toMillis());
    }

    public final void setRespawnDuration(Duration respawnDuration) {
        this.respawnDuration = respawnDuration;
    }

    public Set<PlayerCharacter> getAttackers() {
        return Collections.unmodifiableSet(attackers);
    }

    @Override
    protected final void spawn() {
        setHealth(getMaxHealth());
        super.spawn();
        entity.setInstance(getInstance(), getPosition());
        onSpawn();
    }

    @Override
    protected final void despawn() {
        super.despawn();
        onDespawn();
        entity.remove();
    }

    @Override
    public final void setInstance(Instance instance, Pos position) {
        super.setInstance(instance, position);
        entity.setInstance(instance, position);
    }

    @Override
    public void updatePosition(Pos position) {
        super.updatePosition(position);
        onChangePosition(position);
    }

    void tick(long time) {
        updatePosition(entity.getPosition());

        if (isAlive()) {
            brain.tick(time);
        }
    }

    @Override
    public final void setMaxHealth(double maxHealth) {
        super.setMaxHealth(maxHealth);
    }

    @Override
    public final void setHealth(double health) {
        super.setHealth(health);
    }

    @Override
    public final void damage(@NotNull DamageSource source, double amount) {
        super.damage(source, amount);

        if (source instanceof PlayerCharacter pc) {
            attackers.add(pc);
        }

        if (getHealth() == 0.0) {
            die(source);
        } else {
            takeDamage(source);
        }
    }

    @Override
    public final void heal(@NotNull DamageSource source, double amount) {
        super.heal(source, amount);

        onHeal(source);
    }

    @Override
    public void speak(PlayerCharacter pc, Component message) {
        super.speak(pc, message);

        onSpeak(pc);
    }

    @ApiStatus.Internal
    public final void interact(PlayerCharacter pc) {
        onInteract(pc);
    }

    public final void playAnimation(@NotNull CharacterAnimation animation) {
        animation.play(entity);
    }

    protected void onSpawn() {
    }

    protected void onDespawn() {
    }

    protected void onChangePosition(Pos position) {
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter takes damage but
     * does not die as a result.
     */
    protected void onDamage(DamageSource source) {
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter is healed.
     */
    protected void onHeal(DamageSource source) {
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter takes damage and
     * dies as a result.
     */
    protected void onDeath(DamageSource killer) {
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter is interacted
     * with (right-clicked).
     */
    protected void onInteract(PlayerCharacter pc) {
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter speaks.
     */
    protected void onSpeak(PlayerCharacter pc) {
    }

    public void takeDamage(DamageSource source) {
        damageEffect();
        onDamage(source);
    }

    public void die(DamageSource killer) {
        navigator.setPathTo(null);

        deathEffect();
        onDeath(killer);

        for (PlayerCharacter attacker : attackers) {
            attacker.grantExperiencePoints(experiencePoints / attackers.size());
            for (QuestObjective objective : slayQuestObjectives) {
                objective.addProgress(attacker);
            }
        }

        getSpawner().setActive(false);
        getHitbox().remove();

        if (killer instanceof PlayerCharacter pc) {
            if (lootTable != null) {
                lootTable.generate(pc).forEach(loot -> loot.drop(getInstance(), getPosition()));
            }
        }

        SchedulerManager scheduler = getMmorpg().getSchedulerManager();
        scheduler.buildTask(this::remove).delay(getRemovalDelay()).schedule();

        if (respawnDuration != null) {
            scheduler.buildTask(this::respawn)
                    .delay(respawnDuration.plus(getRemovalDelay()))
                    .schedule();
        }
    }

    private void damageEffect() {
        if (entity instanceof ModelEntity modelEntity) {
            modelDamageTint(modelEntity);
        } else {
            entity.damage(DamageType.VOID, 0.0f);
        }
    }

    private void deathEffect() {
        if (entity instanceof ModelEntity modelEntity) {
            modelDamageTint(modelEntity);
        }
        entity.kill();
    }

    private void modelDamageTint(ModelEntity modelEntity) {
        modelEntity.colorize(0xffaaaa);
        if (endDamageTint != null) {
            endDamageTint.cancel();
        }
        endDamageTint = getMmorpg().getSchedulerManager()
                .buildTask(modelEntity::colorizeDefault)
                .delay(Duration.ofMillis(200))
                .schedule();
    }

    private void respawn() {
        getSpawner().setActive(true);
    }
}
