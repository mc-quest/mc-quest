package net.mcquest.server.features;

import com.google.common.base.Predicates;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.*;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.object.ObjectManager;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.PhysicsManager;
import net.mcquest.core.physics.Triggers;
import net.mcquest.core.quest.*;
import net.mcquest.core.ui.Tutorial;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.constants.Maps;
import net.mcquest.server.constants.Quests;
import net.mcquest.server.npc.GuardThomas;
import net.mcquest.server.npc.TrainingDummy;
import net.minestom.server.coordinate.Pos;
import java.time.Duration;

public class WolfwoodOutpost {
    private Mmorpg mmorpg;

    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        adventurers();
      }

    private void adventurers() {
        Pos[] positions = {
            new Pos(3269, 121.0, 3605, 175.2f, 17.5f)
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.ELADRADOR, position, Adventurer::new));
        }
    }
}