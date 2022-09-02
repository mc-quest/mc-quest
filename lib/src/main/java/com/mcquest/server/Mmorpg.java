package com.mcquest.server;

import com.mcquest.server.character.CharacterEntityManager;
import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.item.Item;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.quest.Quest;
import com.mcquest.server.quest.QuestManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.timer.SchedulerManager;

import java.util.function.Function;

public class Mmorpg {
    private final WorldManager worldManager;
    private final ItemManager itemManager;
    private final QuestManager questManager;
    private final PlayerClassManager playerClassManager;
    private final NonPlayerCharacterSpawner npcSpawner;
    private final CharacterEntityManager characterEntityManager;
    private final PhysicsManager physicsManager;
    private final Feature[] features;

    public Mmorpg(Item[] items, Quest[] quests, PlayerClass[] playerClasses,
                  Feature[] features, Function<Player, PlayerCharacterData> pcDataRetriever) {
        itemManager = new ItemManager(items);
        questManager = new QuestManager();
        playerClassManager = new PlayerClassManager();
        npcSpawner = new NonPlayerCharacterSpawner();
        characterEntityManager = new CharacterEntityManager();
        physicsManager = new PhysicsManager();
        this.features = features.clone();
    }

    public void start(String address, int port) {
        if (MinecraftServer.isStarted()) {
            throw new IllegalStateException("Already started");
        }
        MinecraftServer server = MinecraftServer.init();
        for (Feature feature : features) {
            feature.hook(this);
        }
        server.start(address, port);
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public PlayerClassManager getPlayerClassManager() {
        return playerClassManager;
    }

    public NonPlayerCharacterSpawner getNonPlayerCharacterSpawner() {
        return npcSpawner;
    }

    public CharacterEntityManager getCharacterEntityManager() {
        return characterEntityManager;
    }

    public PhysicsManager getPhysicsManager() {
        return physicsManager;
    }

    public GlobalEventHandler getGlobalEventHandler() {
        return MinecraftServer.getGlobalEventHandler();
    }

    public SchedulerManager getSchedulerManager() {
        return MinecraftServer.getSchedulerManager();
    }
}
