package com.mcquest.server.mount;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.character.PlayerCharacterManager;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.play.ClientSteerVehiclePacket;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class MountManager {
    private final Mmorpg mmorpg;
    private final Map<Integer, Mount> mounts;

    @ApiStatus.Internal
    public MountManager(Mmorpg mmorpg, Mount... mounts) {
        this.mmorpg = mmorpg;
        this.mounts = new HashMap<>();
        for (Mount mount : mounts) {
            registerMount(mount);
        }
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerPacketEvent.class, this::handlePacket);
    }

    private void registerMount(Mount mount) {
        int id = mount.getId();
        if (mounts.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        mounts.put(id, mount);
    }

    public Mount getMount(int id) {
        return mounts.get(id);
    }

    private void handlePacket(PlayerPacketEvent event) {
        ClientPacket packet = event.getPacket();
        if (!(packet instanceof ClientSteerVehiclePacket vehiclePacket)) {
            return;
        }
        Player player = event.getPlayer();
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        PlayerCharacter pc = pcManager.getPlayerCharacter(player);
        Mount mount = pc.getMount();
        if (mount == null) {
            return;
        }
        Entity mountEntity = player.getVehicle();
        float forward = vehiclePacket.forward();
        float sideways = vehiclePacket.sideways();
        if (mountEntity.isOnGround()) {

        }
        /*
         * TODO: Listen to ClientSteerVehiclePacket
         * packet.flags() == 1: jump
         * packet.flags() == 2: shift
         */
    }
}
