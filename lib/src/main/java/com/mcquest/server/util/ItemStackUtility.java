package com.mcquest.server.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

public class ItemStackUtility {
    public static ItemStack createItemStack(Material material, Component displayName,
                                            List<? extends Component> lore) {
        return ItemStack.builder(material)
                .meta(builder -> builder.hideFlag(ItemHideFlag.HIDE_ENCHANTS,
                        ItemHideFlag.HIDE_ATTRIBUTES, ItemHideFlag.HIDE_UNBREAKABLE,
                        ItemHideFlag.HIDE_DESTROYS, ItemHideFlag.HIDE_PLACED_ON,
                        ItemHideFlag.HIDE_POTION_EFFECTS, ItemHideFlag.HIDE_DYE))
                .displayName(displayName)
                .lore(prepareLore(lore))
                .build();
    }

    private static List<Component> prepareLore(List<? extends Component> lore) {
        List<Component> result = new ArrayList<>(lore.size());
        for (Component component : lore) {
            if (!component.hasDecoration(TextDecoration.ITALIC)) {
                component = component.decoration(TextDecoration.ITALIC, false);
            }
            result.add(component.colorIfAbsent(NamedTextColor.WHITE));
        }
        return result;
    }
}
