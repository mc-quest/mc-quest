package com.mcquest.core.character;

import com.mcquest.core.entity.EntityHuman;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.minestom.ModelEntity;

public interface CharacterModel {
    @ApiStatus.Internal
    EntityCreature createEntity(NonPlayerCharacter character);

    static CharacterModel of(EntityType model) {
        return character -> new EntityCreature(model) {
            @Override
            public void tick(long time) {
                super.tick(time);
                character.tick(time);
            }
        };
    }

    static CharacterModel of(PlayerSkin model) {
        return character -> new EntityHuman(model) {
            @Override
            public void tick(long time) {
                super.tick(time);
                character.tick(time);
            }
        };
    }

    static CharacterModel of(Model model) {
        return character -> new ModelEntity(model) {
            @Override
            public void tick(long time) {
                super.tick(time);
                character.tick(time);
            }
        };
    }
}
