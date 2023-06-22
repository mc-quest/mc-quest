package com.mcquest.core.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoPacket;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EntityHuman extends EntityCreature {
    private final PlayerSkin skin;
    private final Entity hideNameplate;

    public EntityHuman(PlayerSkin skin) {
        super(EntityType.PLAYER);
        this.skin = skin;
        hideNameplate = new Entity(EntityType.ARMOR_STAND);
        hideNameplate.setInvisible(true);
        hideNameplate.setSilent(true);
        PlayerMeta meta = (PlayerMeta) getEntityMeta();
        meta.setHatEnabled(true);
        meta.setJacketEnabled(true);
        meta.setLeftLegEnabled(true);
        meta.setRightLegEnabled(true);
        meta.setLeftSleeveEnabled(true);
        meta.setRightSleeveEnabled(true);
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos position) {
        hideNameplate.setInstance(instance, position).join();
        return super.setInstance(instance, position).thenRun(() -> addPassenger(hideNameplate));
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        player.sendPacket(getAddPlayerToList());
        removeFromTabList(player.getPlayerConnection());
        super.updateNewViewer(player);
    }

    private PlayerInfoPacket getAddPlayerToList() {
        PlayerInfoPacket.AddPlayer.Property prop = new PlayerInfoPacket.AddPlayer.Property("textures", skin.textures(), skin.signature());
        List<PlayerInfoPacket.AddPlayer.Property> props = List.of(prop);
        return new PlayerInfoPacket(PlayerInfoPacket.Action.ADD_PLAYER,
                new PlayerInfoPacket.AddPlayer(getUuid(), "", props, GameMode.ADVENTURE, 0, Component.empty(), null));
    }

    private void removeFromTabList(PlayerConnection connection) {
        SendablePacket packet = new PlayerInfoPacket(PlayerInfoPacket.Action.REMOVE_PLAYER, new PlayerInfoPacket.RemovePlayer(getUuid()));
        MinecraftServer.getSchedulerManager()
                .buildTask(() -> connection.sendPacket(packet))
                .delay(20, TimeUnit.SERVER_TICK)
                .schedule();
    }

    @Override
    public void remove() {
        super.remove();
        this.hideNameplate.remove();
    }
}