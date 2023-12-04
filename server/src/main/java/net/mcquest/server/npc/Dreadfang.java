package net.mcquest.server.npc;

import net.kyori.adventure.sound.Sound;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.*;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.PlayerCharacterManager;
import net.mcquest.core.character.*;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.particle.ParticleEffects;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Models;
import net.mcquest.server.constants.Music;
import net.mcquest.server.constants.Quests;
import net.mcquest.core.quest.QuestStatus;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;
import net.kyori.adventure.text.Component;
import net.mcquest.core.ui.InteractionSequence;
import net.mcquest.core.ui.Interactions;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

public class Dreadfang extends NonPlayerCharacter {
    private final Mmorpg mmorpg;
    private final Collider bossBattleBounds;
    private final InteractionSequence offerFightSequence;
    private final InteractionSequence admitDefeatSequence;

    public Dreadfang(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.DREADFANG));
        this.mmorpg = mmorpg;
        setName("Dreadfang");
        setLevel(4);
        setMaxHealth(400);
        setMass(200);
        setMovementSpeed(10.0);
        setRemovalDelay(Duration.ofMillis(2000));
        setRespawnDuration(Duration.ofSeconds(60));
        setExperiencePoints(50);

        offerFightSequence = InteractionSequence.builder()
                .interaction(Interactions.speak(
                        this,
                        Component.text("Ahhh! An Adventurer has broken into my chambers!")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Just as I was hoping...")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Not what you were expecting to hear, huh?")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("hehehe...")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Yes, yes... you're at least somewhat strong.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Exactly how strong remains to be seen! HEHE!")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("You see... my rotten husband has survived"
                                + " for far too long now! I'd have liked to have killed and eaten"
                                + " him YEARS ago! HEHE!")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("I've been looking for a sturdy adventurer like yourself"
                                + " to help me defeat him...")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("But you must defeat MEE first! HEHE!")
                ))
                .interaction(Interactions.addProgress(Quests.DREADFANGS_REVENGE.getObjective(0)))
                .build();

        admitDefeatSequence = InteractionSequence.builder()
                .interaction(Interactions.speak(
                        this,
                        Component.text("Aarghh!! I must catch my breath!")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Just as I was hoping... you ARE strong!")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("You MUST help me slay my husband!")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("And perhaps... then...")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("We'll be free to wed each other!")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("HEHE! A joke. Don't look so horrified!")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("I'll lead you to him. FOLLOW MEEEE!!!!")
                ))
                .interaction(Interactions.addProgress(Quests.DREADFANGS_REVENGE.getObjective(1)))
                .build();

        bossBattleBounds = new Collider(getInstance(), getPosition(), new Vec(25, 10, 25));
    }

    @Override
    public void onDamage(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_HOGLIN_HURT, Sound.Source.HOSTILE, 2f, 0.75f));
        if (getHealth() <= 250.0) {
            Set<PlayerCharacter> attackers = getAttackers();
            for (PlayerCharacter attacker : attackers) {
                admitDefeat(attacker);
            }
        }
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        if (Quests.DREADFANGS_REVENGE.getStatus(pc) == QuestStatus.COMPLETED) {
            speak(pc, Component.text("Thanks again for your help, Adventurer!"
                + " My husband made a pleasing feast! HEHE!"));
        } else if ((Quests.DREADFANGS_REVENGE.getStatus(pc) != QuestStatus.NOT_STARTED)
                && !Quests.DREADFANGS_REVENGE.getObjective(0).isComplete(pc)) {
            offerFightSequence.advance(pc);
        } else if (Quests.DREADFANGS_REVENGE.getObjective(0).isComplete(pc)
                && !Quests.DREADFANGS_REVENGE.getObjective(1).isComplete(pc)) {

            speak(pc, Component.text("FIGHT MEEEE!!!!!!"));
            getMmorpg().getPhysicsManager().addCollider(bossBattleBounds);
            Triggers.playerCharacter(this::enterBossBattle);

            setBrain(ActiveSelector.of(
                    Sequence.of(
                            TaskFindClosestTarget.of(25.0),
                            TaskPlayAnimation.of(CharacterAnimation.named("walk")),
                            SimpleParallel.of(
                                    TaskFollowTarget.of(4.0, 15.0),
                                    Sequence.of(
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_STEP,
                                                    Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                            TaskWait.of(Duration.ofMillis(500))
                                    )
                            ),
                            TaskWait.of(Duration.ofMillis(100)),
                            RandomSelector.of(
                                    new int[]{1, 1, 1},
                                    Sequence.of(
                                            TaskPlayAnimation.of(CharacterAnimation.named("attack 1")),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_ATTACK,
                                                    Sound.Source.HOSTILE, 1f, 1f)),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskAction.of(this::attack1),
                                            TaskWait.of(Duration.ofMillis(800))
                                    ),
                                    Sequence.of(
                                            TaskPlayAnimation.of(CharacterAnimation.named("attack 2")),
                                            TaskWait.of(Duration.ofMillis(300)),
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_ATTACK,
                                                    Sound.Source.HOSTILE, 1f, 1f)),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskAction.of(this::attack2),
                                            TaskWait.of(Duration.ofMillis(800))
                                    ),
                                    Sequence.of(
                                            TaskPlayAnimation.of(CharacterAnimation.named("attack 3")),
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_ATTACK,
                                                    Sound.Source.HOSTILE, 1.5f, 0.75f)),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskAction.of(this::attack3),
                                            TaskWait.of(Duration.ofMillis(800))
                                    ),
                                    Sequence.of(
                                            TaskPlayAnimation.of(CharacterAnimation.named("kick 1")),
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_ATTACK,
                                                    Sound.Source.HOSTILE, 1.5f, 0.75f)),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskAction.of(this::attack3),
                                            TaskWait.of(Duration.ofMillis(800))
                                    ),
                                    Sequence.of(
                                            TaskPlayAnimation.of(CharacterAnimation.named("kick 2")),
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_ATTACK,
                                                    Sound.Source.HOSTILE, 1.5f, 0.75f)),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskAction.of(this::attack3),
                                            TaskWait.of(Duration.ofMillis(800))
                                    )
                            )
                    ),
                    Sequence.of(
                            TaskPlayAnimation.of(CharacterAnimation.named("idle")),
                            TaskWait.of(Duration.ofSeconds(2)),
                            TaskPlayAnimation.of(CharacterAnimation.named("walk")),
                            SimpleParallel.of(
                                    TaskGoToRandomPosition.of(10),
                                    Sequence.of(
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_STEP,
                                                    Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                            TaskWait.of(Duration.ofMillis(500))
                                    )
                            )
                    )
            ));

            bossBattleBounds.onCollisionEnter(Triggers.playerCharacter(this::enterBossBattle));
            bossBattleBounds.onCollisionExit(Triggers.playerCharacter(this::exitBossBattle));
        } else if (Quests.DREADFANGS_REVENGE.getObjective(1).isComplete(pc)) {
            setBrain(ActiveSelector.of(
                    Sequence.of(
                            TaskFindClosestTarget.of(200.0), // Target is now Grimrot
                            TaskPlayAnimation.of(CharacterAnimation.named("walk")),
                            SimpleParallel.of(
                                    TaskFollowTarget.of(4.0, 15.0),
                                    Sequence.of(
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_STEP,
                                                    Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                            TaskWait.of(Duration.ofMillis(500))
                                    )
                            ),
                            TaskWait.of(Duration.ofMillis(100)),
                            RandomSelector.of(
                                    new int[]{1, 1, 1},
                                    Sequence.of(
                                            TaskPlayAnimation.of(CharacterAnimation.named("attack 1")),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_ATTACK,
                                                    Sound.Source.HOSTILE, 1f, 1f)),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskAction.of(this::attack1),
                                            TaskWait.of(Duration.ofMillis(800))
                                    ),
                                    Sequence.of(
                                            TaskPlayAnimation.of(CharacterAnimation.named("attack 2")),
                                            TaskWait.of(Duration.ofMillis(300)),
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_ATTACK,
                                                    Sound.Source.HOSTILE, 1f, 1f)),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskAction.of(this::attack2),
                                            TaskWait.of(Duration.ofMillis(800))
                                    ),
                                    Sequence.of(
                                            TaskPlayAnimation.of(CharacterAnimation.named("attack 3")),
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_ATTACK,
                                                    Sound.Source.HOSTILE, 1.5f, 0.75f)),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskAction.of(this::attack3),
                                            TaskWait.of(Duration.ofMillis(800))
                                    ),
                                    Sequence.of(
                                            TaskPlayAnimation.of(CharacterAnimation.named("kick 1")),
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_ATTACK,
                                                    Sound.Source.HOSTILE, 1.5f, 0.75f)),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskAction.of(this::attack3),
                                            TaskWait.of(Duration.ofMillis(800))
                                    ),
                                    Sequence.of(
                                            TaskPlayAnimation.of(CharacterAnimation.named("kick 2")),
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_ATTACK,
                                                    Sound.Source.HOSTILE, 1.5f, 0.75f)),
                                            TaskWait.of(Duration.ofMillis(250)),
                                            TaskAction.of(this::attack3),
                                            TaskWait.of(Duration.ofMillis(800))
                                    )
                            )
                    ),
                    Sequence.of(
                            TaskPlayAnimation.of(CharacterAnimation.named("idle")),
                            TaskWait.of(Duration.ofSeconds(2)),
                            TaskPlayAnimation.of(CharacterAnimation.named("walk")),
                            SimpleParallel.of(
                                    TaskGoToRandomPosition.of(10),
                                    Sequence.of(
                                            TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_STEP,
                                                    Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                            TaskWait.of(Duration.ofMillis(500))
                                    )
                            )
                    )
            ));
        } else {
            speak(pc, Component.text("HEHE!"));
        }
    }

    private void admitDefeat(PlayerCharacter pc) {
        setBrain(ActiveSelector.of());
        bossBattleBounds.remove();
        admitDefeatSequence.advance(pc);
    }

    @Override
    public Attitude getAttitude(Character other) {
        Collection<PlayerCharacter> pcs =
                mmorpg.getPlayerCharacterManager().getNearbyPlayerCharacters(getInstance(),
                                                                             getPosition(), 25.0);
        PlayerCharacter pc = (PlayerCharacter)pcs.toArray()[0];
        if (!Quests.DREADFANGS_REVENGE.getObjective(1).isComplete(pc)) {
                return (other instanceof GoblinMinion
                        || other instanceof Grimrot
                        || other instanceof Dreadfang)
                        ? Attitude.FRIENDLY
                        : Attitude.HOSTILE;
        } else {
                return (other instanceof GoblinMinion
                        || other instanceof PlayerCharacter
                        || other instanceof Dreadfang)
                        ? Attitude.FRIENDLY
                        : Attitude.HOSTILE;
        }
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    @Override
    protected void onChangePosition(Pos position) {
        Collection<PlayerCharacter> pcs =
                mmorpg.getPlayerCharacterManager().getNearbyPlayerCharacters(getInstance(),
                                                                             getPosition(), 22.5);
        for (PlayerCharacter pc : pcs) {
                if (Quests.DREADFANGS_REVENGE.getObjective(0).isComplete(pc)
                        && !Quests.DREADFANGS_REVENGE.getObjective(1).isComplete(pc)) {
                bossBattleBounds.setCenter(position);
                }
        }
    }

    @Override
    protected void onDeath(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_HOGLIN_DEATH, Sound.Source.HOSTILE, 2f, 0.6f));
        playAnimation(CharacterAnimation.named("death"));
    }

    private void enterBossBattle(PlayerCharacter pc) {
        getBossHealthBar().addViewer(pc);
        pc.getMusicPlayer().setSong(Music.GOBLIN_BATTLE);
    }

    private void exitBossBattle(PlayerCharacter pc) {
        getBossHealthBar().removeViewer(pc);
        pc.getMusicPlayer().setSong(Music.KINGS_DEATH_ROW);
    }

    private boolean attack1(long time) {
        Pos hitboxCenter = getPosition().add(getLookDirection().mul(3)).withY(y -> y + 1.5);
        Vec extents = new Vec(3.25, 3, 3.25);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::attack1Hit));
        return true;
    }

    private void attack1Hit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 7);
            character.applyImpulse(getLookDirection().mul(200));
        }
    }

    private boolean attack2(long time) {
        Pos hitboxCenter = getPosition().add(getLookDirection().mul(3)).withY(y -> y + 1.5);
        Vec extents = new Vec(3.25, 3, 3.25);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::attack2Hit));
        return true;
    }

    private void attack2Hit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 7);
            character.applyImpulse(getLookDirection().mul(100));
        }
    }

    private boolean attack3(long time) {
        Pos hitboxCenter = getPosition().add(getLookDirection().mul(3)).withY(y -> y + 1.5);
        Vec extents = new Vec(7, 4, 7);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::attack3Hit));
        return true;
    }

    private void attack3Hit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 15);
            character.applyImpulse(getLookDirection().mul(2000));
        }
    }

    private boolean kick1(long time) {
        Pos hitboxCenter = getPosition().add(getLookDirection().mul(3)).withY(y -> y + 1.5);
        Vec extents = new Vec(7, 4, 7);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::kick1Hit));
        return true;
    }

    private void kick1Hit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 9);
            character.applyImpulse(getLookDirection().mul(2000));
        }
    }

    private boolean kick2(long time) {
        Pos hitboxCenter = getPosition().add(getLookDirection().mul(3)).withY(y -> y + 1.5);
        Vec extents = new Vec(7, 4, 7);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::kick2Hit));
        return true;
    }

    private void kick2Hit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 9);
            character.applyImpulse(getLookDirection().mul(2000));
        }
    }
}