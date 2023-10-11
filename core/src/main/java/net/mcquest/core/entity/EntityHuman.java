package net.mcquest.core.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoPacket;
import net.minestom.server.timer.TaskSchedule;
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
        removeFromTabList(player);
        super.updateNewViewer(player);
    }

    private PlayerInfoPacket addPlayerToList() {
        PlayerInfoPacket.AddPlayer.Property prop =
                new PlayerInfoPacket.AddPlayer.Property("textures",
                        skin.textures(), skin.signature());
        List<PlayerInfoPacket.AddPlayer.Property> props = List.of(prop);

        return new PlayerInfoPacket(PlayerInfoPacket.Action.ADD_PLAYER,
                new PlayerInfoPacket.AddPlayer(getUuid(), "", props,
                        GameMode.ADVENTURE, 0, Component.empty(), null));
    }

    private void removeFromTabList(Player player) {
        SendablePacket packet = new PlayerInfoPacket(
                PlayerInfoPacket.Action.REMOVE_PLAYER,
                new PlayerInfoPacket.RemovePlayer(getUuid()));

        MinecraftServer.getSchedulerManager()
                .buildTask(() -> player.sendPacket(packet))
                .delay(TaskSchedule.tick(20))
                .schedule();
    }
}
