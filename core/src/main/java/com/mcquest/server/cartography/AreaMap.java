package com.mcquest.server.cartography;

import com.mcquest.server.quest.QuestMarker;
import net.minestom.server.coordinate.Pos;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

public class AreaMap {
    private final int id;
    private final Pos origin;
    private final BufferedImage image;
    private final Collection<QuestMarker> questMarkers;

    public AreaMap(int id, Pos origin, Callable<InputStream> image) {
        try {
            this.id = id;
            this.origin = origin;
            InputStream inputStream = image.call();
            this.image = ImageIO.read(inputStream);
            inputStream.close();
            this.questMarkers = new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        return id;
    }

    public void addQuestMarker(QuestMarker questMarker) {
        questMarkers.add(questMarker);
    }

    void render() {
        // TODO: figure out args
    }
}
