package com.mcquest.server.text;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.mcquest.server.text.TextSerializer.deserialize;

public class WordWrap {
    public static final int STANDARD_LINE_LENGTH = 18;

    public static List<TextComponent> wrap(@NotNull String text) {
        return wrap(text, STANDARD_LINE_LENGTH);
    }

    public static List<TextComponent> wrap(@NotNull String text, int lineLength) {
        List<String> tokens = tokenize(text, lineLength);

        List<TextComponent> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        int currentLineLength = 0;
        String chatColor = "";

        for (String token : tokens) {
            if (token.equals("\n")) {
                removeTrailingSpace(currentLine);
                lines.add(deserialize(currentLine.toString()));
                currentLine.setLength(0);
                currentLineLength = 0;
                chatColor = ChatColor.getLastColors(chatColor);
                currentLine.append(chatColor);
            } else if (token.equals(" ")) {
                currentLine.append(' ');
                currentLineLength++;
            } else if (token.charAt(0) == ChatColor.COLOR_CHARACTER) {
                currentLine.append(token);
                chatColor += token;
            } else {
                if (currentLineLength + token.length() > lineLength) {
                    removeTrailingSpace(currentLine);
                    lines.add(deserialize(currentLine.toString()));
                    currentLine.setLength(0);
                    currentLineLength = 0;
                    chatColor = ChatColor.getLastColors(chatColor);
                    currentLine.append(chatColor);
                }
                currentLine.append(token);
                currentLineLength += token.length();
            }
        }

        lines.add(deserialize(currentLine.toString()));

        return lines;
    }

    static List<String> tokenize(String text, int maxWordLength) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder(maxWordLength);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                if (!currentWord.isEmpty()) {
                    tokens.add(currentWord.toString());
                    currentWord.setLength(0);
                }
                tokens.add("\n");
            } else if (ch == ' ') {
                if (!currentWord.isEmpty()) {
                    tokens.add(currentWord.toString());
                    currentWord.setLength(0);
                }
                tokens.add(" ");
            } else if (ch == ChatColor.COLOR_CHARACTER) {
                if (!currentWord.isEmpty()) {
                    tokens.add(currentWord.toString());
                    currentWord.setLength(0);
                }
                if (text.length() < i + 2) {
                    throw new IllegalArgumentException("Missing color code");
                }
                char code = text.charAt(i + 1);
                if (ChatColor.forCode(code) == null) {
                    throw new IllegalArgumentException("Invalid color code: " + code);
                }
                tokens.add(text.substring(i, i + 2));
                i++;
            } else {
                currentWord.append(ch);
                if (currentWord.length() == maxWordLength) {
                    tokens.add(currentWord.toString());
                    currentWord.setLength(0);
                }
            }
        }

        if (!currentWord.isEmpty()) {
            tokens.add(currentWord.toString());
        }

        return tokens;
    }

    private static void removeTrailingSpace(StringBuilder line) {
        int trailingSpaceCount = 0;
        while (line.charAt(line.length() - 1 - trailingSpaceCount) == ' ') {
            trailingSpaceCount++;
        }
        line.setLength(line.length() - trailingSpaceCount);
    }
}
