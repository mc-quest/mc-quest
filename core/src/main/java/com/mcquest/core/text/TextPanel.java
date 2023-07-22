package com.mcquest.core.text;

import com.mcquest.core.instance.Instance;
import com.mcquest.core.object.Object;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.hologram.Hologram;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TextPanel extends Object {
    private static final double DEFAULT_LINE_HEIGHT = 0.25;

    private List<Component> text;
    private double lineHeight;
    private final List<Hologram> holograms;

    public TextPanel(Instance instance, Pos position) {
        super(instance, position);
        text = Collections.emptyList();
        lineHeight = DEFAULT_LINE_HEIGHT;
        holograms = new ArrayList<>();
    }

    @Override
    public void setInstance(@NotNull Instance instance) {
        Instance oldInstance = getInstance();

        super.setInstance(instance);

        if (instance == oldInstance) {
            return;
        }

        if (!isSpawned()) {
            return;
        }

        holograms.forEach(Hologram::remove);
        holograms.clear();

        for (int i = 0; i < text.size(); i++) {
            Component line = text.get(i);
            Hologram hologram = new Hologram(instance, linePosition(i), line);
            holograms.add(hologram);
        }
    }

    @Override
    public void setPosition(@NotNull Pos position) {
        super.setPosition(position);

        if (!isSpawned()) {
            return;
        }

        for (int i = 0; i < holograms.size(); i++) {
            Hologram hologram = holograms.get(i);
            hologram.setPosition(linePosition(i));
        }
    }

    public List<Component> getText() {
        return Collections.unmodifiableList(text);
    }

    public void setText(@NotNull List<? extends Component> text) {
        this.text = new ArrayList<>(text);

        if (!isSpawned()) {
            return;
        }

        for (int i = 0; i < text.size(); i++) {
            Component line = text.get(i);
            if (i < holograms.size()) {
                Hologram hologram = holograms.get(i);
                hologram.setText(line);
            } else {
                Hologram hologram = new Hologram(getInstance(), linePosition(i), line);
                holograms.add(hologram);
            }
        }

        while (holograms.size() > text.size()) {
            holograms.remove(holograms.size() - 1).remove();
        }
    }

    public double getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(double lineHeight) {
        this.lineHeight = lineHeight;

        if (!isSpawned()) {
            return;
        }

        for (int i = 0; i < holograms.size(); i++) {
            Hologram hologram = holograms.get(i);
            hologram.setPosition(linePosition(i));
        }
    }

    @Override
    protected void spawn() {
        super.spawn();

        for (int i = 0; i < text.size(); i++) {
            Component line = text.get(i);
            Hologram hologram = new Hologram(getInstance(), linePosition(i), line);
            holograms.add(hologram);
        }
    }

    @Override
    protected void despawn() {
        super.despawn();

        holograms.forEach(Hologram::remove);
        holograms.clear();
    }

    private Pos linePosition(int line) {
        return getPosition().withY(y -> y - line * lineHeight);
    }
}
