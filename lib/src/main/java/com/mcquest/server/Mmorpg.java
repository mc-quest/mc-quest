package com.mcquest.server;

import com.mcquest.server.cartography.AreaMap;
import com.mcquest.server.cartography.MapManager;
import com.mcquest.server.character.CharacterEntityManager;
import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.item.Item;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.music.MusicManager;
import com.mcquest.server.music.Song;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.quest.Quest;
import com.mcquest.server.quest.QuestManager;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.resource.ResourceManager;
import com.mcquest.server.ui.InteractionHandler;
import com.mcquest.server.ui.PlayerCharacterLogoutType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.timer.SchedulerManager;
import team.unnamed.hephaestus.Model;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class Mmorpg {
    private final MinecraftServer server;
    private final PlayerClassManager playerClassManager;
    private final ItemManager itemManager;
    private final QuestManager questManager;
    private final MusicManager musicManager;
    private final MapManager mapManager;
    private final InstanceManager instanceManager;
    private final ResourceManager resourceManager;
    private final PlayerCharacterManager pcManager;
    private final NonPlayerCharacterSpawner npcSpawner;
    private final CharacterEntityManager characterEntityManager;
    private final PhysicsManager physicsManager;
    private final Feature[] features;

    private Mmorpg(Builder builder) {
        server = builder.server;
        playerClassManager = new PlayerClassManager(this, builder.playerClasses);
        itemManager = new ItemManager(builder.items);
        questManager = new QuestManager(builder.quests);
        musicManager = new MusicManager(builder.music);
        mapManager = new MapManager(builder.maps);
        instanceManager = new InstanceManager(builder.instances);
        resourceManager = new ResourceManager(builder.models);
        pcManager = new PlayerCharacterManager(this,
                builder.pcDataProvider, builder.pcLogoutHandler);
        npcSpawner = new NonPlayerCharacterSpawner();
        characterEntityManager = new CharacterEntityManager();
        physicsManager = new PhysicsManager();
        features = builder.features;
        InteractionHandler interactionHandler = new InteractionHandler(this);
        interactionHandler.registerListeners();
    }

    private void start(String address, int port, int resourcePackServerPort) {
        for (Feature feature : features) {
            feature.hook(this);
        }
        resourceManager.startResourcePackServer(address, resourcePackServerPort);
        server.start(address, port);
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

    public MapManager getMapManager() {
        return mapManager;
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

    public GlobalEventHandler getGlobalEventHandler() {
        return MinecraftServer.getGlobalEventHandler();
    }

    public SchedulerManager getSchedulerManager() {
        return MinecraftServer.getSchedulerManager();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final MinecraftServer server;
        private PlayerClass[] playerClasses;
        private Item[] items;
        private Quest[] quests;
        private Song[] music;
        private AreaMap[] maps;
        private Instance[] instances;
        private Model[] models;
        private Feature[] features;
        private Function<Player, PlayerCharacterData> pcDataProvider;
        private BiConsumer<PlayerCharacter, PlayerCharacterLogoutType> pcLogoutHandler;

        private Builder() {
            server = MinecraftServer.init();
            playerClasses = new PlayerClass[0];
            items = new Item[0];
            quests = new Quest[0];
            music = new Song[0];
            maps = new AreaMap[0];
            instances = new Instance[0];
            models = new Model[0];
            features = new Feature[0];
            pcDataProvider = null;
            pcLogoutHandler = null;
        }

        public Builder playerClasses(PlayerClass... playerClasses) {
            this.playerClasses = playerClasses.clone();
            return this;
        }

        public Builder items(Item... items) {
            this.items = items.clone();
            return this;
        }

        public Builder quests(Quest... quests) {
            this.quests = quests.clone();
            return this;
        }

        public Builder music(Song... music) {
            this.music = music.clone();
            return this;
        }

        public Builder maps(AreaMap... maps) {
            this.maps = maps.clone();
            return this;
        }

        public Builder instances(Instance... instances) {
            this.instances = instances.clone();
            return this;
        }

        public Builder models(Model... models) {
            this.models = models.clone();
            return this;
        }

        public Builder features(Feature... features) {
            this.features = features.clone();
            return this;
        }

        public Builder playerCharacterDataProvider(Function<Player,
                PlayerCharacterData> dataProvider) {
            this.pcDataProvider = dataProvider;
            return this;
        }

        public Builder playerCharacterLogoutHandler(
                BiConsumer<PlayerCharacter, PlayerCharacterLogoutType> logoutHandler) {
            this.pcLogoutHandler = logoutHandler;
            return this;
        }

        public void start(String address, int port, int resourcePackServerPort) {
            if (pcDataProvider == null) {
                throw new NullPointerException("You need to specify a player character data provider");
            }
            if (pcLogoutHandler == null) {
                throw new NullPointerException("You need to specify a player character logout handler");
            }
            Mmorpg mmorpg = new Mmorpg(this);
            mmorpg.start(address, port, resourcePackServerPort);
        }
    }
}
