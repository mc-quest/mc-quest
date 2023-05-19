package com.mcquest.server.commerce;

public class Money {
    private final int value;

    public Money(int value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public static Money copper(int copper) {
        return new Money(copper);
    }

    public static Money silver(int silver) {
        return new Money(silver * 100);
    }

    public static Money gold(int gold) {
        return new Money(gold * 100 * 100);
    }

    public int getValue() {
        return value;
    }

    public int getCopperPart() {
        return value % 100;
    }

    public int getSilverPart() {
        return (value / 100) % 100;
    }

    public int getGoldPart() {
        return value / (100 * 100);
    }

    public Money add(Money money) {
        return new Money(value + money.value);
    }

    public Money subtract(Money money) {
        return new Money(value - money.value);
    }
}
