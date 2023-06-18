package com.mcquest.server.combat;

import java.util.HashMap;
import java.util.Map;

public class Damage {
    private final Map<Element, Double> amounts;

    private Damage() {
        amounts = new HashMap<>();
    }

    public double getAmount(Element element) {
        return amounts.get(element);
    }
}
