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
import com.mcquest.server.playerclass.Skill;
import com.mcquest.server.quest.Quest;
import com.mcquest.server.quest.QuestManager;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.resourcepack.ResourcePackManager;
import com.mcquest.server.ui.InteractionHandler;
import com.mcquest.server.ui.PlayerCharacterLogoutType;
import com.mcquest.server.zone.Zone;
import com.mcquest.server.zone.ZoneManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.timer.SchedulerManager;
import team.unnamed.creative.file.FileTree;
import team.unnamed.hephaestus.Model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Mmorpg {
    private final MinecraftServer server;
    private final PlayerClassManager playerClassManager;
    private final ItemManager itemManager;
    private final QuestManager questManager;
    private final ZoneManager zoneManager;
    private final MusicManager musicManager;
    private final MapManager mapManager;
    private final InstanceManager instanceManager;
    private final PlayerCharacterManager pcManager;
    private final NonPlayerCharacterSpawner npcSpawner;
    private final CharacterEntityManager characterEntityManager;
    private final PhysicsManager physicsManager;
    private final Feature[] features;
    private final ResourcePackManager resourcePackManager;

    private Mmorpg(Builder builder) {
        server = builder.server;
        playerClassManager = new PlayerClassManager(this, builder.playerClasses);
        itemManager = new ItemManager(builder.items);
        questManager = new QuestManager(builder.quests);
        zoneManager = new ZoneManager(builder.zones);
        musicManager = new MusicManager(builder.music);
        mapManager = new MapManager(builder.maps);
        instanceManager = new InstanceManager(builder.instances);
        Collection<Skill> skills = new ArrayList<>();
        for (PlayerClass playerClass : builder.playerClasses) {
            skills.addAll(playerClass.getSkills());
        }
        pcManager = new PlayerCharacterManager(this,
                builder.pcDataProvider, builder.pcLogoutHandler);
        npcSpawner = new NonPlayerCharacterSpawner();
        characterEntityManager = new CharacterEntityManager();
        physicsManager = new PhysicsManager();
        features = builder.features;
        resourcePackManager = new ResourcePackManager(builder.resourcePackWriter,
                skills.toArray(new Skill[0]), builder.items, builder.music, builder.models);
        InteractionHandler interactionHandler = new InteractionHandler(this);
        interactionHandler.registerListeners();
    }

    private void start(String address, int port, int resourcePackServerPort) {
        for (Feature feature : features) {
            feature.hook(this);
        }
        resourcePackManager.startServer(address, resourcePackServerPort);
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

    public ZoneManager getZoneManager() {
        return zoneManager;
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

    public ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final MinecraftServer server;
        private PlayerClass[] playerClasses;
        private Item[] items;
        private Quest[] quests;
        private Zone[] zones;
        private Song[] music;
        private AreaMap[] maps;
        private Instance[] instances;
        private Model[] models;
        private Feature[] features;
        private Consumer<FileTree> resourcePackWriter;
        private Function<Player, PlayerCharacterData> pcDataProvider;
        private BiConsumer<PlayerCharacter, PlayerCharacterLogoutType> pcLogoutHandler;

        private Builder() {
            server = MinecraftServer.init();
            playerClasses = new PlayerClass[0];
            items = new Item[0];
            quests = new Quest[0];
            zones = new Zone[0];
            music = new Song[0];
            maps = new AreaMap[0];
            instances = new Instance[0];
            models = new Model[0];
            features = new Feature[0];
            resourcePackWriter = null;
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

        public Builder zones(Zone... zones) {
            this.zones = zones.clone();
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

        public Builder resourcePack(Consumer<FileTree> writer) {
            resourcePackWriter = writer;
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
                throw new NullPointerException("No player character data provider specified");
            }
            if (pcLogoutHandler == null) {
                throw new NullPointerException("No player character logout handler specified");
            }
            if (resourcePackWriter == null) {
                throw new NullPointerException("No resource pack writer specified");
            }
            Mmorpg mmorpg = new Mmorpg(this);
            mmorpg.start(address, port, resourcePackServerPort);
        }
    }
}
