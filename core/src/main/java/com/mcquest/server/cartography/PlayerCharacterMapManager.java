package com.mcquest.server.cartography;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.MapCloseEvent;
import com.mcquest.server.event.MapOpenEvent;
import com.mcquest.server.item.Weapon;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.MapMeta;

public class PlayerCharacterMapManager {
    private final PlayerCharacter pc;
    private AreaMap map;
    private boolean mapIsOpen;

    public PlayerCharacterMapManager(PlayerCharacter pc) {
        this.pc = pc;
    }

    public AreaMap getMap() {
        return map;
    }

    public void setMap(AreaMap map) {
        this.map = map;
        if (isMapOpen()) {
            map.render(pc);
        }
    }

    public boolean isMapOpen() {
        return mapIsOpen;
    }

    public void openMap() {
        pc.getInventory().saveWeapon();
        Player player = pc.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack mapItemStack = ItemStack.builder(Material.FILLED_MAP)
                .meta(new MapMeta.Builder().mapId(AreaMap.MAP_ID).build())
                .build();
        inventory.setItemInMainHand(mapItemStack);
        map.render(pc);
        MapOpenEvent event = new MapOpenEvent(pc);
        MinecraftServer.getGlobalEventHandler().call(event);
        mapIsOpen = true;
    }

    public void closeMap() {
        pc.getInventory().unsaveWeapon();
        MapCloseEvent event = new MapCloseEvent(pc);
        MinecraftServer.getGlobalEventHandler().call(event);
        mapIsOpen = false;
    }
}
