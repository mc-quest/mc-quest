package net.mcquest.core.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

public class ItemStackUtility {
    public static ItemStack.Builder create(Material material, Component displayName,
                                           List<? extends Component> lore) {
        return ItemStack.builder(material)
                .meta(builder -> builder.hideFlag(ItemHideFlag.values()))
                .displayName(prepareComponent(displayName))
                .lore(prepareLore(lore));
    }

    private static Component prepareComponent(Component component) {
        if (!component.hasDecoration(TextDecoration.ITALIC)) {
            component = component.decoration(TextDecoration.ITALIC, false);
        }
        return component.colorIfAbsent(NamedTextColor.WHITE);
    }

    private static List<Component> prepareLore(List<? extends Component> lore) {
        return lore.stream().map(ItemStackUtility::prepareComponent).toList();
    }
}
