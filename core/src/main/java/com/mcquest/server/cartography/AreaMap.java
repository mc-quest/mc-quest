package com.mcquest.server.cartography;

import com.mcquest.server.asset.Asset;
import com.mcquest.server.asset.AssetTypes;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.quest.Quest;
import com.mcquest.server.quest.QuestMarker;
import com.mcquest.server.quest.QuestMarkerIcon;
import com.mcquest.server.zone.Zone;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.map.framebuffers.Graphics2DFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AreaMap {
    static final int MAP_ID = 1;
    private static final int MAP_WIDTH = 128;
    private static final double QUEST_MARKER_TOP_PADDING = 10.0;
    private static final double QUEST_MARKER_RIGHT_PADDING = 7.0;
    private static final double QUEST_MARKER_BOTTOM_PADDING = 1.0;
    private static final double QUEST_MARKER_LEFT_PADDING = 0.0;

    private final int id;
    private final Pos origin;
    private final BufferedImage image;
    private final Collection<QuestMarker> questMarkers;

    public AreaMap(int id, Pos origin, Asset image) {
        try {
            this.id = id;
            this.origin = origin;
            image.requireType(AssetTypes.PNG);
            this.image = image.readImage();
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

        renderMap(renderer, pc);
        renderQuestMarkers(renderer, pc);
        renderZoneText(renderer, pc);

        MapDataPacket packet = framebuffer.preparePacket(1);
        packet = addCursor(packet, pc);
        Player player = pc.getPlayer();
        player.sendPacket(packet);
    }

    private void renderMap(Graphics2D renderer, PlayerCharacter pc) {
        Pos position = pc.getPosition();
        int imageStartX = Math.max(0, (int) (position.x() - origin.x() - MAP_WIDTH / 2.0));
        int imageStartY = Math.max(0, (int) (position.z() - origin.z() - MAP_WIDTH / 2.0));
        if (imageStartX < image.getWidth() && imageStartY < image.getHeight()) {
            int width = Math.min(image.getWidth() - imageStartX, MAP_WIDTH);
            int height = Math.min(image.getHeight() - imageStartY, MAP_WIDTH);
            BufferedImage subimage = image.getSubimage(imageStartX, imageStartY, width, height);
            int renderX = Math.max(0, (int) (origin.x() - position.x() + MAP_WIDTH / 2.0));
            int renderY = Math.max(0, (int) (origin.z() - position.z() + MAP_WIDTH / 2.0));
            renderer.drawImage(subimage, renderX, renderY, null);
        }
    }

    private void renderQuestMarkers(Graphics2D renderer, PlayerCharacter pc) {
        renderer.setColor(Color.YELLOW);
        for (QuestMarker questMarker : questMarkers) {
            renderQuestMarker(renderer, pc, questMarker);
        }
    }

    private void renderQuestMarker(Graphics2D renderer, PlayerCharacter pc, QuestMarker questMarker) {
        Pos position = pc.getPosition();
        List<Quest> trackedQuests = pc.getQuestTracker().getTrackedQuests();

        if (!(trackedQuests.contains(questMarker.getQuest()) && questMarker.shouldShow(pc))) {
            return;
        }

        String questMarkerText = switch (questMarker.getIcon()) {
            case READY_TO_START -> "!";
            case READY_TO_TURN_IN -> "?";
            case OBJECTIVE -> String.valueOf(trackedQuests.indexOf(questMarker.getQuest()) + 1);
        };

        Pos markerPosition = questMarker.getPosition();
        double renderX = markerPosition.x() - position.x();
        double renderY = markerPosition.z() - position.z();

        if (questMarker.getIcon() != QuestMarkerIcon.READY_TO_START) {
            if (renderX == 0.0) {
                renderY = Math.max(renderY, -MAP_WIDTH / 2.0 + QUEST_MARKER_TOP_PADDING);
                renderY = Math.min(renderY, MAP_WIDTH / 2.0 - QUEST_MARKER_BOTTOM_PADDING);
            } else if (renderY == 0.0) {
                renderX = Math.max(renderX, -MAP_WIDTH / 2.0 + QUEST_MARKER_LEFT_PADDING);
                renderX = Math.min(renderX, MAP_WIDTH / 2.0 - QUEST_MARKER_RIGHT_PADDING);
            } else {
                double tan = renderY / renderX;
                if (renderX < -MAP_WIDTH / 2.0 + QUEST_MARKER_LEFT_PADDING) {
                    renderX = -MAP_WIDTH / 2.0 + QUEST_MARKER_LEFT_PADDING;
                    renderY = renderX * tan;
                } else if (renderX > MAP_WIDTH / 2.0 - QUEST_MARKER_RIGHT_PADDING) {
                    renderX = MAP_WIDTH / 2.0 - QUEST_MARKER_RIGHT_PADDING;
                    renderY = renderX * tan;
                }
                if (renderY < -MAP_WIDTH / 2.0 + QUEST_MARKER_TOP_PADDING) {
                    renderY = -MAP_WIDTH / 2.0 + QUEST_MARKER_TOP_PADDING;
                    renderX = renderY / tan;
                } else if (renderY > MAP_WIDTH / 2.0 - QUEST_MARKER_BOTTOM_PADDING) {
                    renderY = MAP_WIDTH / 2.0 - QUEST_MARKER_BOTTOM_PADDING;
                    renderX = renderY / tan;
                }
            }
        }

        renderX += MAP_WIDTH / 2.0;
        renderY += MAP_WIDTH / 2.0;

        renderer.drawString(questMarkerText, (int) renderX, (int) renderY);
    }

    private void renderZoneText(Graphics2D renderer, PlayerCharacter pc) {
        Zone zone = pc.getZone();
        renderer.setColor(zone.getType().getMapColor());
        renderer.drawString(zone.getName(), 0, 10);
    }

    private MapDataPacket addCursor(MapDataPacket packet, PlayerCharacter pc) {
        Vec lookDirection = pc.getLookDirection();
        double theta = Math.atan2(lookDirection.z(), lookDirection.x()) - Math.PI / 2.0;
        if (theta < 0.0) {
            theta = 2.0 * Math.PI + theta;
        }
        byte cursorDirection = (byte) ((theta + Math.PI / 16.0) / (Math.PI / 8.0));
        int playerCursorIcon = 0;
        MapDataPacket.Icon cursor = new MapDataPacket.Icon(playerCursorIcon, (byte) 0, (byte) 0,
                cursorDirection, null);
        List<MapDataPacket.Icon> icons = List.of(cursor);
        MapDataPacket withCursor = new MapDataPacket(packet.mapId(), packet.scale(),
                packet.locked(), true, icons, packet.colorContent());
        return withCursor;
    }
}
