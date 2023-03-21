package com.mcquest.server.cartography;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.quest.Quest;
import com.mcquest.server.quest.QuestMarker;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.map.framebuffers.Graphics2DFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class AreaMap {
    static final int MAP_ID = 1;

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

    void render(PlayerCharacter pc) {
        Graphics2DFramebuffer framebuffer = new Graphics2DFramebuffer();
        Graphics2D renderer = framebuffer.getRenderer();

        // Draw map.
        Pos position = pc.getPosition();
        int startX = Math.max(0, (int) (position.x() - origin.x()));
        int startY = Math.max(0, (int) (position.z() - origin.z()));
        if (startX < image.getWidth() && startY < image.getHeight()) {
            int width = Math.min(image.getWidth() - startX, 128);
            int height = Math.min(image.getHeight() - startY, 128);
            BufferedImage subimage = image.getSubimage(startX, startY, width, height);
            int renderX = Math.max(0, (int) (origin.x() - position.x()));
            int renderY = Math.max(0, (int) (origin.z() - position.z()));
            renderer.drawImage(subimage, renderX, renderY, null);
        }
        Player player = pc.getPlayer();

        // Draw quest markers.
        for (QuestMarker questMarker : questMarkers) {
            List<Quest> trackedQuests = pc.getQuestTracker().getTrackedQuests();
            if (questMarker.shouldShow(pc)) {
                String questMarkerText = switch (questMarker.getIcon()) {
                    case READY_TO_START -> "!";
                    case READY_TO_TURN_IN -> "?";
                    case OBJECTIVE -> String.valueOf(trackedQuests.indexOf(questMarker.getQuest()) + 1);
                };
                renderer.drawString(questMarkerText, 0, 0);
            }
        }

        // Draw zone text.
        renderer.setColor(Color.YELLOW);
        renderer.drawString(pc.getZone().getName(), 0, 10);

        // Draw player cursor.
        MapDataPacket packet = framebuffer.preparePacket(1);
        // packet.icons().add(new MapDataPacket.Icon(1, (byte) 1, (byte) 2, (byte) 1, null));
        player.sendPacket(packet);
    }
}
