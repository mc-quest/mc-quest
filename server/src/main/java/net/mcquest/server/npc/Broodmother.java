package net.mcquest.server.npc;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.*;
import net.mcquest.core.character.*;
import net.mcquest.core.character.Character;
import net.mcquest.core.loot.ItemPoolEntry;
import net.mcquest.core.loot.LootTable;
import net.mcquest.core.loot.Pool;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Items;
import net.mcquest.server.constants.Models;
import net.mcquest.server.constants.Music;
import net.kyori.adventure.sound.Sound;
import net.mcquest.server.constants.Quests;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class Broodmother extends NonPlayerCharacter {
    private final Collider bossBattleBounds;

    public Broodmother(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.BROODMOTHER));
        setName("Broodmother");
        setLevel(6);
        setMaxHealth(250);
        setMass(20);
        setMovementSpeed(10.0);
        setRemovalDelay(Duration.ofMillis(2000));
        setRespawnDuration(Duration.ofSeconds(45));
        setExperiencePoints(100);
        addSlayQuestObjective(Quests.NOT_SO_ITSY_BITSY.getObjective(0));
        setLootTable(LootTable.builder()
                .pool(Pool.builder()
                        .entry(ItemPoolEntry.builder(Items.ADVENTURERS_SWORD).build())
                        .build())
                .build());

        setBrain(ActiveSelector.of(
                Sequence.of(
                        TaskFindClosestTarget.of(25.0),
                        TaskPlayAnimation.of(CharacterAnimation.named("walk")),
                        SimpleParallel.of(
                                TaskFollowTarget.of(4.0, 15.0),
                                Sequence.of(
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_SPIDER_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        TaskWait.of(Duration.ofMillis(500))
                                )
                        ),
                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_EVOKER_FANGS_ATTACK, Sound.Source.HOSTILE,
                                1f, 1f)),
                        TaskPlayAnimation.of(CharacterAnimation.named("attack")),
                        TaskWait.of(Duration.ofMillis(500)),
                        TaskAction.of(this::attack),
                        TaskWait.of(Duration.ofMillis(800))
                ),
                Sequence.of(
                        TaskPlayAnimation.of(CharacterAnimation.named("idle")),
                        TaskWait.of(Duration.ofSeconds(2)),
                        TaskPlayAnimation.of(CharacterAnimation.named("walk")),
                        SimpleParallel.of(
                                TaskGoToRandomPosition.of(10),
                                Sequence.of(
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_SPIDER_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        TaskWait.of(Duration.ofMillis(500))
                                )
                        )
                )
        ));

        bossBattleBounds = new Collider(getInstance(), getPosition(), new Vec(50, 30, 50));
        bossBattleBounds.onCollisionEnter(Triggers.playerCharacter(this::enterBossBattle));
        bossBattleBounds.onCollisionExit(Triggers.playerCharacter(this::exitBossBattle));
    }

    @Override
    public Attitude getAttitude(Character other) {
        return (other instanceof Spider || other instanceof Broodmother ||
                other instanceof SpiderEgg || other instanceof SpiderEggCluster)
                ? Attitude.FRIENDLY
                : Attitude.HOSTILE;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    @Override
    protected void onSpawn() {
        getMmorpg().getPhysicsManager().addCollider(bossBattleBounds);
    }

    @Override
    protected void onDespawn() {
        bossBattleBounds.remove();
    }

    @Override
    protected void onChangePosition(Pos position) {
        bossBattleBounds.setCenter(position);
    }

    protected void onDamage(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_SPIDER_HURT, Sound.Source.HOSTILE, 2f, 0.75f));
    }

    @Override
    protected void onDeath(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_SPIDER_DEATH, Sound.Source.HOSTILE, 2f, 0.75f));
    }

    private void enterBossBattle(PlayerCharacter pc) {
        getBossHealthBar().addViewer(pc);
        pc.getMusicPlayer().setSong(Music.BROODMOTHER_BATTLE);
    }

    private void exitBossBattle(PlayerCharacter pc) {
        getBossHealthBar().removeViewer(pc);
        pc.getMusicPlayer().setSong(Music.BROODMOTHER_LAIR);
    }

    private boolean attack(long time) {
        Pos position = getPosition();
        Pos hitboxCenter = position.add(position.direction().mul(2.5)).withY(y -> y + 1.0);
        Vec extents = new Vec(3.0, 1.5, 3.0);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::attackHit));
        return true;
    }

    private void attackHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 9.5);
            character.applyImpulse(getPosition().direction().mul(2000.0));
        }
    }
}
