package com.mcquest.server.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public class TextUtility {
    private static final LegacyComponentSerializer TEXT_SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand();

    public static TextComponent deserializeText(String text) {
        return TEXT_SERIALIZER.deserialize(text);
    }

    public static String serializeText(Component text) {
        return TEXT_SERIALIZER.serialize(text);
    }

    /**
     * Splits the text into lines while preserving text color. If the text is
     * null, null is returned.
     */
    public static Component[] wordWrap(String text, int lineLength) {
        if (text == null) {
            return null;
        }
        if (lineLength <= 0) {
            throw new IllegalArgumentException("Nonpositive line length: " + lineLength);
        }
        if (text.isEmpty()) {
            return new Component[0];
        }
        List<String> lines = new ArrayList<>();
        // TODO
        return lines.toArray(new Component[lines.size()]);
    }
}
