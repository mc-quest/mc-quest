package com.mcquest.server.music;

public enum Pitch {
    F_SHARP_1(0.5f),
    G_FLAT_1(0.5f),
    G_1(0.529732f),
    G_SHARP_1(0.561231f),
    A_FLAT_1(0.561231f),
    A_1(0.594604f),
    A_SHARP_1(0.629961f),
    B_FLAT_1(0.629961f),
    B_1(0.667420f),
    B_SHARP_1(0.707107f),
    C_FLAT_1(0.667420f),
    C_1(0.707107f),
    C_SHARP_1(0.749154f),
    D_FLAT_1(0.749154f),
    D_1(0.793701f),
    D_SHARP_1(0.840896f),
    E_FLAT_1(0.840896f),
    E_1(0.890899f),
    E_SHARP_1(0.943874f),
    F_FLAT_1(0.890899f),
    F_1(0.943874f),
    F_SHARP_2(1f),
    G_FLAT_2(1f),
    G_2(1.059463f),
    G_SHARP_2(1.122462f),
    A_FLAT_2(1.122462f),
    A_2(1.189207f),
    A_SHARP_2(1.259921f),
    B_FLAT_2(1.259921f),
    B_2(1.334840f),
    B_SHARP_2(1.414214f),
    C_FLAT_2(1.334840f),
    C_2(1.414214f),
    C_SHARP_2(1.498307f),
    D_FLAT_2(1.498307f),
    D_2(1.587401f),
    D_SHARP_2(1.681793f),
    E_FLAT_2(1.681793f),
    E_2(1.781797f),
    E_SHARP_2(1.887749f),
    F_FLAT_2(1.781797f),
    F_2(1.887749f),
    F_SHARP_3(2f),
    G_FLAT_3(2f);

    private final float value;

    Pitch(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
