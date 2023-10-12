package net.mcquest.core.cartography;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.MapOpenEvent;
import net.mcquest.core.event.MapCloseEvent;
import net.mcquest.core.persistence.PlayerCharacterData;
import net.mcquest.core.zone.Zone;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.MapMeta;
import net.minestom.server.map.framebuffers.Graphics2DFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.awt.Graphics2D;
import java.util.List;

public class MapViewer {
    private static final int MAP_ID = 1;

    private final PlayerCharacter pc;
    private Map map;
    private boolean open;

    public MapViewer(PlayerCharacter pc, PlayerCharacterData data,
                     MapManager mapManager) {
        this.pc = pc;
        map = null;
        open = false;
    }

    public @Nullable Map getMap() {
        return map;
    }

    public void setMap(@Nullable Map map) {
        this.map = map;
    }

    public boolean isOpen() {
        return open;
    }

    @ApiStatus.Internal
    public void open() {
        if (open) {
            return;
        }

        pc.getInventory().saveWeapon();

        Player player = pc.getEntity();
        PlayerInventory inventory = player.getInventory();
        ItemStack mapItemStack = ItemStack.builder(Material.FILLED_MAP)
                .meta(new MapMeta.Builder().mapId(MAP_ID).build())
                .build();
        inventory.setItemInMainHand(mapItemStack);

        MapOpenEvent event = new MapOpenEvent(pc);
        MinecraftServer.getGlobalEventHandler().call(event);

        open = true;
    }

    @ApiStatus.Internal
    public void close() {
        if (!open) {
            return;
        }

        pc.getInventory().unsaveWeapon();

        MapCloseEvent event = new MapCloseEvent(pc);
        MinecraftServer.getGlobalEventHandler().call(event);

        open = false;
    }

    void render() {
        Graphics2DFramebuffer framebuffer = new Graphics2DFramebuffer();
        Graphics2D renderer = framebuffer.getRenderer();

        if (map != null) {
            map.render(pc, renderer);
        }

        renderZoneText(renderer);

        MapDataPacket packet = framebuffer.preparePacket(MAP_ID);
        packet = addCursor(packet);

        Player player = pc.getEntity();
        player.sendPacket(packet);
    }

    private void renderZoneText(Graphics2D renderer) {
        Zone zone = pc.getZone();
        renderer.setColor(zone.getType().getMapColor());
        renderer.drawString(zone.getName(), 0, 10);
    }

    private MapDataPacket addCursor(MapDataPacket packet) {
        Vec lookDirection = pc.getLookDirection();
        double theta = Math.atan2(lookDirection.z(), lookDirection.x()) - Math.PI / 2.0;
        if (theta < 0.0) {
            theta = 2.0 * Math.PI + theta;
        }
        byte cursorDirection = (byte) ((theta + Math.PI / 16.0) / (Math.PI / 8.0));

        int cursorIcon = 0;
        MapDataPacket.Icon cursor = new MapDataPacket.Icon(cursorIcon, (byte) 0,
                (byte) 0, cursorDirection, null);
        List<MapDataPacket.Icon> icons = List.of(cursor);

        return new MapDataPacket(packet.mapId(), packet.scale(),
                packet.locked(), true, icons, packet.colorContent());
    }
}
