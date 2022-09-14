package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.item.ArmorItem;
import com.mcquest.server.item.ConsumableItem;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.item.Weapon;

public class Items implements Feature {
    private Weapon adventurersBlade;
    private ArmorItem adventurersCap;
    private ConsumableItem minorPotionOfHealing;

    @Override
    public void hook(Mmorpg mmorpg) {
        ItemManager itemManager = mmorpg.getItemManager();
    }

    public Weapon adventurersBlade() {
        return adventurersBlade;
    }

    public ArmorItem adventurersCap() {
        return adventurersCap;
    }

    public ConsumableItem minorPotionOfHealing() {
        return minorPotionOfHealing;
    }
}
