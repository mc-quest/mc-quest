package com.mcquest.core.weather;

import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.ChangeGameStatePacket;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;

public class Weather {
    private final float rainLevel;
    private final float thunderLevel;

    private Weather(float rainLevel, float thunderLevel) {
        if (rainLevel < 0.0f || rainLevel > 1.0f
                || thunderLevel < 0.0f || thunderLevel > 1.0f) {
            throw new IllegalArgumentException();
        }
        this.rainLevel = rainLevel;
        this.thunderLevel = thunderLevel;
    }

    public static Weather clear() {
        return new Weather(0.0f, 0.0f);
    }

    public static Weather rain(float rainLevel) {
        return new Weather(rainLevel, 0.0f);
    }

    public static Weather thunder(float rainLevel, float thunderLevel) {
        return new Weather(rainLevel, thunderLevel);
    }

    public float getRainLevel() {
        return rainLevel;
    }

    public float getThunderLevel() {
        return thunderLevel;
    }

    @ApiStatus.Internal
    public Collection<SendablePacket> getPackets() {
        return List.of(
                new ChangeGameStatePacket(
                        ChangeGameStatePacket.Reason.RAIN_LEVEL_CHANGE,
                        rainLevel
                ),
                new ChangeGameStatePacket(
                        ChangeGameStatePacket.Reason.THUNDER_LEVEL_CHANGE,
                        thunderLevel
                )
        );
    }
}
