package com.mcquest.core.cinema;

import java.util.function.Function;

import static java.lang.Math.*;

/**
 * See https://easings.net/ for explanations.
 */
public enum Interpolation {
    LINEAR(t -> t),
    EASE_IN_SINE(t -> 1 - cos(t * PI) / 2),
    EASE_OUT_SINE(t -> sin(t * PI) / 2),
    EASE_IN_OUT_SINE(t -> -(cos(PI * t) - 1) / 2),
    EASE_IN_QUAD(t -> t * t),
    EASE_OUT_QUAD(t -> 1 - (1 - t) * (1 - t)),
    EASE_IN_OUT_QUAD(t -> t < 0.5 ? 2 * t * t : 1 - pow(-2 * t + 2, 2) / 2),
    EASE_IN_CUBIC(t -> t * t * t),
    EASE_OUT_CUBIC(t -> 1 - pow(1 - t, 3)),
    EASE_IN_OUT_CUBIC(t -> t < 0.5 ? 4 * t * t * t : 1 - pow(-2 * t + 2, 3) / 2),
    EASE_IN_QUART(t -> t * t * t * t),
    EASE_OUT_QUART(t -> 1 - pow(1 - t, 4)),
    EASE_IN_OUT_QUART(t -> t < 0.5 ? 8 * t * t * t * t : 1 - pow(-2 * t + 2, 4) / 2),
    EASE_IN_QUINT(t -> t * t * t * t * t),
    EASE_OUT_QUINT(t -> 1 - pow(1 - t, 5)),
    EASE_IN_OUT_QUINT(t -> t < 0.5 ? 16 * t * t * t * t * t : 1 - pow(-2 * t + 2, 5) / 2),
    EASE_IN_EXPO(t -> t == 0 ? 0 : pow(2, 10 * t - 10)),
    EASE_OUT_EXPO(t -> t == 1 ? 1 : 1 - pow(2, -10 * t)),
    EASE_IN_OUT_EXPO(t -> t == 0 ? 0 : t == 1 ? 1 : t < 0.5 ? pow(2, 20 * t - 10) / 2 : (2 - pow(2, -20 * t + 10)) / 2),
    EASE_IN_CIRC(t -> 1 - sqrt(1 - pow(t, 2))),
    EASE_OUT_CIRC(t -> sqrt(1 - pow(t - 1, 2))),
    EASE_IN_OUT_CIRC(t -> t < 0.5 ? (1 - sqrt(1 - pow(2 * t, 2))) / 2 : (sqrt(1 - pow(-2 * t + 2, 2)) + 1) / 2),
    EASE_IN_BACK(t -> 2.70158 * t * t * t - 1.70158 * t * t),
    EASE_OUT_BACK(t -> 1 + 2.70158 * pow(t - 1, 3) + 1.70158 * pow(t - 1, 2)),
    EASE_IN_OUT_BACK(t -> t < 0.5 ? (pow(2 * t, 2) * ((1.70158 * 1.525 + 1) * 2 * t - 1.70158 * 1.525)) / 2 :
            (pow(2 * t - 2, 2) * ((1.70158 * 1.525 + 1) * (t * 2 - 2) + 1.70158 * 1.525) + 2) / 2),
    EASE_IN_ELASTIC(t -> t == 0 ? 0 : t == 1 ? 1 : -pow(2, 10 * t - 10) * sin((t * 10 - 10.75) * ((2 * PI) / 3))),
    EASE_OUT_ELASTIC(t -> t == 0 ? 0 : t == 1 ? 1 : pow(2, -10 * t) * sin((t * 10 - 0.75) * ((2 * PI) / 3)) + 1),
    EASE_IN_OUT_ELASTIC(t -> t == 0 ? 0 : t == 1 ? 1 : t < 0.5 ?
            -(pow(2, 20 * t - 10) * sin((20 * t - 11.125) * ((2 * PI) / 4.5))) / 2 :
            (pow(2, -20 * t + 10) * sin((20 * t - 11.125) * ((2 * PI) / 4.5))) / 2 + 1);

    private final Function<Double, Double> function;

    Interpolation(Function<Double, Double> function) {
        this.function = function;
    }

    public double interpolate(double t) {
        return function.apply(t);
    }
}
