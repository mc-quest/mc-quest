package net.mcquest.core.zone;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.weather.Weather;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Zone {
    private final String id;
    private final String name;
    private final int level;
    private final ZoneType type;
    private final Set<PlayerCharacter> pcs;
    private Weather weather;

    public Zone(String id, String name, int level, ZoneType type) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.type = type;
        this.pcs = new HashSet<>();
        weather = Weather.clear();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public ZoneType getType() {
        return type;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
        pcs.forEach(this::sendWeatherPackets);
    }

    public Set<PlayerCharacter> getPlayerCharacters() {
        return Collections.unmodifiableSet(pcs);
    }

    @ApiStatus.Internal
    public void addPlayerCharacter(PlayerCharacter pc) {
        pcs.add(pc);
        showZoneText(pc);
        MinecraftServer.getSchedulerManager()
                .buildTask(() -> sendWeatherPackets(pc))
                .delay(TaskSchedule.nextTick())
                .schedule();
    }

    @ApiStatus.Internal
    public void removePlayerCharacter(PlayerCharacter pc) {
        pcs.remove(pc);
    }

    private void showZoneText(PlayerCharacter pc) {
        Component zoneText = Component.text(name, type.getTextColor());
        Component levelText = Component.text("Level " + level, NamedTextColor.GOLD);
        Duration fadeIn = Duration.ofSeconds(1);
        Duration stay = Duration.ofSeconds(3);
        Duration fadeOut = Duration.ofSeconds(1);
        Title.Times times = Title.Times.times(fadeIn, stay, fadeOut);
        Title title = Title.title(zoneText, levelText, times);
        pc.getEntity().showTitle(title);
    }

    private void sendWeatherPackets(PlayerCharacter pc) {
        pc.getEntity().sendPackets(weather.getPackets());
    }
}
