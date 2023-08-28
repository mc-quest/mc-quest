package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.Attitude;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.DamageSource;
import com.mcquest.core.entity.CharacterEntityManager;
import com.mcquest.core.entity.EntityHuman;
import com.mcquest.core.instance.Instance;
import com.mcquest.server.constants.Skins;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.timer.TaskSchedule;

import java.time.Duration;

public class BroodmotherLairAdventurer extends DamageableEntityCharacter {
    private static final Vec SIZE = new Vec(1.0, 2.0, 1.0);

    public BroodmotherLairAdventurer(Mmorpg mmorpg, Instance instance, Pos spawnPosition) {
        super(mmorpg, instance, spawnPosition, SIZE);
        setName("Adventurer");
        setLevel(4);
        setMaxHealth(100);
        setHealth(getMaxHealth());
    }

    @Override
    public Attitude getAttitude(Character other) {
        return other instanceof Spider ? Attitude.HOSTILE : Attitude.FRIENDLY;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return source instanceof Spider; // TODO other spiders
    }

    @Override
    protected void onDeath(DamageSource source) {
        super.onDeath(source);

        BroodmotherLairAdventurer adventurer =
                new BroodmotherLairAdventurer(mmorpg, getInstance(), spawnPosition);
        mmorpg.getSchedulerManager().buildTask(() -> mmorpg.getObjectManager().add(adventurer))
                .delay(TaskSchedule.seconds(15)).schedule();
    }

    private boolean shouldAttack(Character other) {
        return getAttitude(other) == Attitude.HOSTILE && other.isDamageable(this);
    }

    private void attack(Character other) {
        if (!isAlive()) {
            return;
        }

        other.damage(this, 5.0);
    }

    @Override
    protected EntityCreature createEntity() {
        return new AdventurerEntity();
    }

    private class AdventurerEntity extends EntityHuman {
        public AdventurerEntity() {
            super(Skins.ADVENTURER_MALE);
            CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
            addAIGroup(new EntityAIGroupBuilder()
                    .addTargetSelector(new ClosestEntityTarget(this, 10,
                            characterEntityManager.entityPredicate(BroodmotherLairAdventurer.this::shouldAttack)))
                    .addGoalSelector(new MeleeAttackGoal(this, 1.5, Duration.ofSeconds(1)))
                    .build());
            eventNode().addListener(EntityAttackEvent.class,
                    characterEntityManager.entityAttackListener(BroodmotherLairAdventurer.this,
                            BroodmotherLairAdventurer.this::attack));
        }

        @Override
        public void update(long time) {
            super.update(time);
            BroodmotherLairAdventurer.this.updatePosition(getPosition());
        }
    }
}
