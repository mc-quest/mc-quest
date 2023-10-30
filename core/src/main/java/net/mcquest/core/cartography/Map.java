package net.mcquest.core.cartography;

import net.mcquest.core.asset.Asset;
import net.mcquest.core.asset.AssetTypes;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.quest.Quest;
import net.mcquest.core.quest.QuestMarker;
import net.mcquest.core.quest.QuestMarkerIcon;
import net.minestom.server.coordinate.Pos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Map {
    private static final int MAP_WIDTH = 128;
    private static final double QUEST_MARKER_TOP_PADDING = 10.0;
    private static final double QUEST_MARKER_RIGHT_PADDING = 7.0;
    private static final double QUEST_MARKER_BOTTOM_PADDING = 1.0;
    private static final double QUEST_MARKER_LEFT_PADDING = 0.0;

    private final String id;
    private final Pos origin;
    private final Asset image;
    private final BufferedImage bufferedImage;
    private final Collection<QuestMarker> questMarkers;

    private Map(String id, Pos origin, Asset image) {
        this.id = id;
        this.origin = origin;
        image.requireType(AssetTypes.PNG);
        this.image = image;
        this.bufferedImage = image.readImage();
        this.questMarkers = new ArrayList<>();
    }

    public static Map of(String id, Pos origin, Asset image) {
        return new Map(id, origin, image);
    }

    public String getId() {
        return id;
    }

    public Pos getOrigin() {
        return origin;
    }

    public Asset getImage() {
        return image;
    }

    public Collection<QuestMarker> getQuestMarkers() {
        return Collections.unmodifiableCollection(questMarkers);
    }

    public void addQuestMarker(QuestMarker questMarker) {
        questMarkers.add(questMarker);
    }

    void render(PlayerCharacter pc, Graphics2D renderer) {
        renderMap(renderer, pc);
        renderQuestMarkers(renderer, pc);
    }

    private void renderMap(Graphics2D renderer, PlayerCharacter pc) {
        Pos position = pc.getPosition();
        int imageStartX = Math.max(0, (int) (position.x() - origin.x() - MAP_WIDTH / 2.0));
        int imageStartY = Math.max(0, (int) (position.z() - origin.z() - MAP_WIDTH / 2.0));
        if (imageStartX < bufferedImage.getWidth() && imageStartY < bufferedImage.getHeight()) {
            int width = Math.min(bufferedImage.getWidth() - imageStartX, MAP_WIDTH);
            int height = Math.min(bufferedImage.getHeight() - imageStartY, MAP_WIDTH);
            BufferedImage subimage = bufferedImage.getSubimage(imageStartX, imageStartY, width, height);
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
}
