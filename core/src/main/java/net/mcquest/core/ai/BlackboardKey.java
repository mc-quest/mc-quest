package net.mcquest.core.ai;

public class BlackboardKey<T> {
    private final String key;

    private BlackboardKey(String key) {
        this.key = key;
    }

    public static <T> BlackboardKey<T> of(String key) {
        return new BlackboardKey<>(key);
    }

    public String getKey() {
        return key;
    }
}
