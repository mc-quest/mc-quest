package com.mcquest.server;

import com.mcquest.server.character.CharacterEntityManager;
import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.feature.FeatureManager;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.quest.QuestManager;
import com.mcquest.server.instance.InstanceManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.timer.SchedulerManager;

public class Mmorpg {
    private boolean isStarted;
    private final PlayerCharacterManager pcManager;
    private final InstanceManager instanceManager;
    private final ItemManager itemManager;
    private final QuestManager questManager;
    private final PlayerClassManager playerClassManager;
    private final NonPlayerCharacterSpawner npcSpawner;
    private final CharacterEntityManager characterEntityManager;
    private final PhysicsManager physicsManager;
    private final FeatureManager featureManager;

    public Mmorpg() {
        isStarted = false;
        pcManager = new PlayerCharacterManager(this);
        instanceManager = new InstanceManager();
        itemManager = new ItemManager();
        questManager = new QuestManager();
        playerClassManager = new PlayerClassManager();
        npcSpawner = new NonPlayerCharacterSpawner();
        characterEntityManager = new CharacterEntityManager();
        physicsManager = new PhysicsManager();
        featureManager = new FeatureManager();
    }

    public void start(String address, int port) {
        MinecraftServer server = MinecraftServer.init();
        pcManager.registerEvents();
        instanceManager.unloadVacantChunks();
        for (Feature feature : featureManager.getFeatures()) {
            feature.hook(this);
        }
        server.start(address, port);
        isStarted = true;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public PlayerCharacterManager getPlayerCharacterManager() {
        return pcManager;
    }

    public InstanceManager getInstanceManager() {
        return instanceManager;
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
