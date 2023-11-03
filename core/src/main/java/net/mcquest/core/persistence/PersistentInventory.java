package net.mcquest.core.persistence;

public record PersistentInventory(
        String weaponId,
        String feetArmorId,
        String legsArmorId,
        String chestArmorId,
        String headArmorId,
        PersistentItem hotbarConsumable1,
        PersistentItem hotbarConsumable2,
        PersistentItem[] storage
) {}
