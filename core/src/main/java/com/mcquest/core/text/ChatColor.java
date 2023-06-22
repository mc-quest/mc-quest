package com.mcquest.core.text;

import java.util.HashMap;
import java.util.Map;

/**
 * Placeholder for Bukkit's ChatColor API, which supports inserting colors into
 * components as strings.
 */
public enum ChatColor {
    BLACK('0', false),
    DARK_BLUE('1', false),
    DARK_GREEN('2', false),
    DARK_AQUA('3', false),
    DARK_RED('4', false),
    DARK_PURPLE('5', false),
    GOLD('6', false),
    GRAY('7', false),
    DARK_GRAY('8', false),
    BLUE('9', false),
    GREEN('a', false),
    AQUA('b', false),
    RED('c', false),
    LIGHT_PURPLE('d', false),
    YELLOW('e', false),
    WHITE('f', false),
    MAGIC('k', true),
    BOLD('l', true),
    STRIKETHROUGH('m', true),
    UNDERLINE('n', true),
    ITALIC('o', true),
    RESET('r', true);

    public static final char COLOR_CHARACTER = '&';
    private static final Map<Character, ChatColor> BY_CODE = new HashMap<>();

    private final char code;
    private final boolean isFormat;
    private final String toString;

    static {
        for (ChatColor chatColor : values()) {
            BY_CODE.put(chatColor.code, chatColor);
        }
    }

    ChatColor(char code, boolean isFormat) {
        this.code = code;
        this.isFormat = isFormat;
        this.toString = new String(new char[]{COLOR_CHARACTER, code});
    }

    public char getCode() {
        return code;
    }

    public boolean isFormat() {
        return isFormat;
    }

    public boolean isColor() {
        return !isFormat;
    }

    public static ChatColor forCode(char code) {
        return BY_CODE.get(code);
    }

    public static String getLastColors(String input) {
        String result = "";
        for (int i = input.length() - 2; i >= 0; i--) {
            char ch = input.charAt(i);
            if (ch == COLOR_CHARACTER) {
                char next = input.charAt(i + 1);
                ChatColor color = forCode(next);
                if (color != null) {
                    result = color + result;
                    // Once we find a color or reset we can stop searching.
                    if (color.isColor() || color == RESET) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return toString;
    }
}
