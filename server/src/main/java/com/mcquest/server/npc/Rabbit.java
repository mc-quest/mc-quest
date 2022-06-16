package com.mcquest.server.npc;

import com.mcquest.server.character.*;
import com.mcquest.server.character.Character;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.metadata.animal.RabbitMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.sound.SoundEvent;

public class Rabbit extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME =
            Component.text("Rabbit", NamedTextColor.GREEN);
    private static final Sound HURT_SOUND =
            Sound.sound(SoundEvent.ENTITY_RABBIT_HURT, Sound.Source.NEUTRAL, 1f, 1f);

    private final Pos spawnPosition;
    private final RabbitMeta.Type type;
    private final CharacterHitbox hitbox;
    private Entity entity;

    public Rabbit(Instance instance, Pos spawnPosition, RabbitMeta.Type type) {
        super(DISPLAY_NAME, 1, instance, spawnPosition);
        this.spawnPosition = spawnPosition;
        this.type = type;
        hitbox = new CharacterHitbox(this, instance, spawnPosition, 0.5, 0.5, 0.5);
        setHeight(0.75);
    }

    @Override
    public void setPosition(Pos position) {
        super.setPosition(position);
        hitbox.setCenter(position.add(0.0, getHeight() / 2.0, 0.0));
    }

    @Override
    protected void spawn() {
        super.spawn();
        entity = new Entity(this);
        CharacterEntityManager.register(entity, this);
        entity.setInstance(getInstance(), getPosition());
        hitbox.setEnabled(true);
    }

    @Override
    protected void despawn() {
        super.despawn();
        hitbox.setEnabled(false);
        CharacterEntityManager.unregister(entity);
        entity.remove();
        setPosition(spawnPosition);
    }

    @Override
    public void damage(DamageSource source, double amount) {
        super.damage(source, amount);
        Pos position = getPosition();
        getInstance().playSound(HURT_SOUND, position.x(), position.y(), position.z());
        entity.damage(DamageType.VOID, 0f);
    }

    @Override
    public boolean isFriendly(Character other) {
        return true;
    }

    public static class Entity extends EntityCreature {
        private final Rabbit rabbit;

        private Entity(Rabbit rabbit) {
            super(EntityType.RABBIT);
            ((RabbitMeta) getEntityMeta()).setType(rabbit.type);
            this.rabbit = rabbit;
            addAIGroup(new EntityAIGroupBuilder()
                    .addGoalSelector(new RandomStrollGoal(this, 5))
                    .build());
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            rabbit.setPosition(getPosition());
        }

        public Rabbit getRabbit() {
            return rabbit;
        }
    }
}
