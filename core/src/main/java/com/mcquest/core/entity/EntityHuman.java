package com.mcquest.core.entity;

import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityHuman extends EntityCreature {
    private final PlayerSkin skin;

    public EntityHuman(@NotNull PlayerSkin skin) {
        super(EntityType.PLAYER);
        this.skin = skin;
        PlayerMeta meta = (PlayerMeta) getEntityMeta();
        meta.setHatEnabled(true);
        meta.setJacketEnabled(true);
        meta.setLeftLegEnabled(true);
        meta.setRightLegEnabled(true);
        meta.setLeftSleeveEnabled(true);
        meta.setRightSleeveEnabled(true);
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        player.sendPacket(addPlayerToList());
        super.updateNewViewer(player);
    }

    private PlayerInfoUpdatePacket addPlayerToList() {
        return new PlayerInfoUpdatePacket(
                PlayerInfoUpdatePacket.Action.ADD_PLAYER,
                addPlayerEntry()
        );
    }

    private PlayerInfoUpdatePacket.Entry addPlayerEntry() {
        return new PlayerInfoUpdatePacket.Entry(
                getUuid(),
                "",
                List.of(textures()),
                false,
                0,
                GameMode.ADVENTURE,
                null,
                null
        );
    }

    private PlayerInfoUpdatePacket.Property textures() {
        return new PlayerInfoUpdatePacket.Property(
                "textures",
                skin.textures(),
                skin.signature()
        );
    }
}
