package com.mcquest.server.cartography;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.item.Weapon;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.MapMeta;
import net.minestom.server.timer.TaskSchedule;

public class PlayerCharacterMapManager {
    private final PlayerCharacter pc;
    private AreaMap map;
    private Weapon savedWeapon;

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
        return savedWeapon != null;
    }

    public void openMap() {
        Player player = pc.getPlayer();
        savedWeapon = pc.getWeapon();
        PlayerInventory inventory = player.getInventory();
        ItemStack mapItemStack = ItemStack.builder(Material.FILLED_MAP)
                .meta(new MapMeta.Builder().mapId(AreaMap.MAP_ID).build())
                .build();
        inventory.setItemInMainHand(mapItemStack);
        MinecraftServer.getSchedulerManager().buildTask(() -> map.render(pc)).delay(TaskSchedule.seconds(1)).schedule();
    }

    public void closeMap() {
        savedWeapon = null;
    }
}
