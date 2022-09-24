package com.mcquest.server;

import com.mcquest.server.character.CharacterEntityManager;
import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.feature.FeatureManager;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.music.MusicManager;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.quest.QuestManager;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.resource.ResourceManager;
import com.mcquest.server.ui.InteractionHandler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.timer.SchedulerManager;

public class Mmorpg {
    private boolean isStarted;
    private final MinecraftServer server;
    private final PlayerClassManager playerClassManager;
    private final ItemManager itemManager;
    private final QuestManager questManager;
    private final MusicManager musicManager;
    private final InstanceManager instanceManager;
    private final ResourceManager resourceManager;
    private final PlayerCharacterManager pcManager;
    private final NonPlayerCharacterSpawner npcSpawner;
    private final CharacterEntityManager characterEntityManager;
    private final PhysicsManager physicsManager;
    private final FeatureManager featureManager;

    public Mmorpg() {
        isStarted = false;
        server = MinecraftServer.init();
        playerClassManager = new PlayerClassManager();
        itemManager = new ItemManager();
        questManager = new QuestManager();
        musicManager = new MusicManager();
        instanceManager = new InstanceManager();
        resourceManager = new ResourceManager();
        pcManager = new PlayerCharacterManager(this);
        npcSpawner = new NonPlayerCharacterSpawner();
        characterEntityManager = new CharacterEntityManager();
        physicsManager = new PhysicsManager();
        featureManager = new FeatureManager();
        InteractionHandler interactionHandler = new InteractionHandler(this);
        interactionHandler.registerListeners();
    }

    public void start(String address, int port, int resourcePackServerPort) {
        for (Feature feature : featureManager.getFeatures()) {
            feature.hook(this);
        }
        resourceManager.startResourcePackServer(address, resourcePackServerPort);
        server.start(address, port);
        isStarted = true;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public PlayerClassManager getPlayerClassManager() {
        return playerClassManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public InstanceManager getInstanceManager() {
        return instanceManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public PlayerCharacterManager getPlayerCharacterManager() {
        return pcManager;
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

    public FeatureManager getFeatureManager() {
        return featureManager;
    }

    public GlobalEventHandler getGlobalEventHandler() {
        return MinecraftServer.getGlobalEventHandler();
    }

    public SchedulerManager getSchedulerManager() {
        return MinecraftServer.getSchedulerManager();
    }
}
