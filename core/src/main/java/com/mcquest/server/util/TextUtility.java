package com.mcquest.server.util;

import com.mcquest.server.ui.ChatColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextUtility {
    public static final int STANDARD_LINE_LENGTH = 18;
    private static final LegacyComponentSerializer TEXT_SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand();

    public static TextComponent deserializeText(String text) {
        return TEXT_SERIALIZER.deserialize(text);
    }

    public static String serializeText(Component text) {
        return TEXT_SERIALIZER.serialize(text);
    }

    public static List<TextComponent> wordWrap(@NotNull String text) {
        return wordWrap(text, STANDARD_LINE_LENGTH);
    }

    /**
     * Splits the text into lines while preserving text color. If the text is
     * null, null is returned.
     */
    public static List<TextComponent> wordWrap(@NotNull String text, int lineLength) {
        if (lineLength <= 0) {
            throw new IllegalArgumentException("Non-positive line length: " + lineLength);
        }
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<TextComponent> lines = new ArrayList<>();
        String[] preLines = text.split("\n");
        for (String preLine : preLines) {
            StringBuilder currentLine = new StringBuilder();
            String[] tokens = preLine.split(" ");
            int currentLineLength = 0;
            for (String token : tokens) {
                int tokenLength = length(token);
                boolean lineEmpty = currentLine.isEmpty();
                // Add 1 if a space character is needed.
                if (currentLineLength + (lineEmpty ? 0 : 1) + tokenLength <= lineLength) {
                    if (!lineEmpty) {
                        currentLine.append(' ');
                    }
                    currentLine.append(token);
                    currentLineLength += tokenLength;
                } else {
                    String currentLineStr = currentLine.toString();
                    lines.add(deserializeText(currentLineStr));
                    currentLine = new StringBuilder(ChatColor.getLastColors(currentLineStr));

                    // Check if token is too long to fit on one line.
                    while ((tokenLength = length(token)) > lineLength) {
                        String[] subTokens = split(token, lineLength);
                        currentLine.append(subTokens[0]);
                        lines.add(deserializeText(currentLine.toString()));
                        token = subTokens[1];
                        currentLine = new StringBuilder(ChatColor.getLastColors(currentLine.toString()));
                    }

                    currentLine.append(token);
                    currentLineLength = tokenLength;
                }
            }
            lines.add(deserializeText(currentLine.toString()));
        }
        return lines;
    }

    private static String[] split(String token, int length) {
        int counter = 0;
        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (ch == ChatColor.COLOR_CHARACTER) {
                // Skip next character.
                i++;
            } else {
                counter++;
                if (counter == length) {
                    return new String[]{token.substring(0, counter), token.substring(counter)};
                }
            }
        }
        throw new IllegalArgumentException("length too large");
    }

    private static int length(String token) {
        int length = 0;
        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (ch == ChatColor.COLOR_CHARACTER) {
                // Skip next character.
                i++;
            } else {
                length++;
            }
        }
        return length;
    }
}
