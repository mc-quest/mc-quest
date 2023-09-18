package com.mcquest.core.character;

import com.mcquest.core.entity.EntityHuman;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.minestomce.GenericBoneEntity;
import team.unnamed.hephaestus.minestomce.ModelEntity;

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
        return of(model, 1f);
    }

    static CharacterModel of(Model model, double scale) {
        return character -> {
            ModelEntity entity = new ModelEntity(EntityType.ARMOR_STAND, model, (float) scale) {
                @Override
                public void tick(long time) {
                    super.tick(time);
                    character.tick(time);
                    bones().forEach(bone -> bone.teleport(getPosition()));
                }

                @Override
                public void remove() {
                    bones().forEach(GenericBoneEntity::remove);
                }
            };
            entity.setNoGravity(false);
            entity.setAutoViewable(true);
            entity.bones().forEach(bone -> bone.setAutoViewable(true));
            return entity;
        };
    }
}
