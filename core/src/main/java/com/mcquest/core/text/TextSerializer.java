package com.mcquest.core.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class TextSerializer {
    private static final LegacyComponentSerializer serializer =
            LegacyComponentSerializer.legacy(ChatColor.COLOR_CHARACTER);

    public static TextComponent deserialize(String text) {
        return serializer.deserialize(text);
    }

    public static String serialize(Component component) {
        return serializer.serialize(component);
    }
}
