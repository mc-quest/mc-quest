package com.mcquest.server;

import com.mcquest.server.cartography.AreaMap;
import com.mcquest.server.cartography.MapManager;
import com.mcquest.server.character.CharacterEntityManager;
import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.instance.InstanceManager;
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
        pcManager = new PlayerCharacterManager(
                this,
                builder.pcDataProvider,
                builder.pcLogoutHandler
        );
        npcSpawner = new NonPlayerCharacterSpawner();
        characterEntityManager = new CharacterEntityManager();
        physicsManager = new PhysicsManager();
        features = builder.features;
        resourcePackManager = new ResourcePackManager(
                builder.resourcePackWriter,
                skills.toArray(new Skill[0]),
                builder.items,
                builder.music,
                builder.models
        );
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

    public static PlayerClassesStep builder() {
        return new Builder();
    }

    public interface PlayerClassesStep {
        ItemsStep playerClasses(PlayerClass... playerClasses);
    }

    public interface ItemsStep {
        QuestsStep items(Item... items);
    }

    public interface QuestsStep {
        ZonesStep quests(Quest... quests);
    }

    public interface ZonesStep {
        MusicStep zones(Zone... zones);
    }

    public interface MusicStep {
        MapsStep music(Song... music);
    }

    public interface MapsStep {
        InstancesStep maps(AreaMap... maps);
    }

    public interface InstancesStep {
        ModelsStep instances(Instance... instances);
    }

    public interface ModelsStep {
        FeaturesStep models(Model... models);
    }

    public interface FeaturesStep {
        ResourcePackStep features(Feature... features);
    }

    public interface ResourcePackStep {
        PlayerCharacterDataProviderStep resourcePack(Consumer<FileTree> writer);
    }

    public interface PlayerCharacterDataProviderStep {
        PlayerCharacterLogoutHandlerStep
        playerCharacterDataProvider(Function<Player, PlayerCharacterData> dataProvider);
    }

    public interface PlayerCharacterLogoutHandlerStep {
        StartStep
        playerCharacterLogoutHandler(BiConsumer<PlayerCharacter, PlayerCharacterLogoutType> logoutHandler);
    }

    public interface StartStep {
        void start(String address, int port, int resourcePackServerPort);
    }

    private static class Builder implements PlayerClassesStep, ItemsStep, QuestsStep, ZonesStep,
            MusicStep, MapsStep, InstancesStep, ModelsStep, FeaturesStep, ResourcePackStep,
            PlayerCharacterDataProviderStep, PlayerCharacterLogoutHandlerStep, StartStep {
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
        }

        @Override
        public ItemsStep playerClasses(PlayerClass... playerClasses) {
            this.playerClasses = playerClasses.clone();
            return this;
        }

        @Override
        public QuestsStep items(Item... items) {
            this.items = items.clone();
            return this;
        }

        @Override
        public ZonesStep quests(Quest... quests) {
            this.quests = quests.clone();
            return this;
        }

        @Override
        public MusicStep zones(Zone... zones) {
            this.zones = zones.clone();
            return this;
        }

        @Override
        public MapsStep music(Song... music) {
            this.music = music.clone();
            return this;
        }

        @Override
        public InstancesStep maps(AreaMap... maps) {
            this.maps = maps.clone();
            return this;
        }

        @Override
        public ModelsStep instances(Instance... instances) {
            this.instances = instances.clone();
            return this;
        }

        @Override
        public FeaturesStep models(Model... models) {
            this.models = models.clone();
            return this;
        }

        @Override
        public ResourcePackStep features(Feature... features) {
            this.features = features.clone();
            return this;
        }

        @Override
        public PlayerCharacterDataProviderStep resourcePack(Consumer<FileTree> writer) {
            resourcePackWriter = writer;
            return this;
        }

        @Override
        public PlayerCharacterLogoutHandlerStep playerCharacterDataProvider(
                Function<Player, PlayerCharacterData> dataProvider) {
            this.pcDataProvider = dataProvider;
            return this;
        }


        @Override
        public StartStep playerCharacterLogoutHandler(
                BiConsumer<PlayerCharacter, PlayerCharacterLogoutType> logoutHandler) {
            this.pcLogoutHandler = logoutHandler;
            return this;
        }

        @Override
        public void start(String address, int port, int resourcePackServerPort) {
            Mmorpg mmorpg = new Mmorpg(this);
            mmorpg.start(address, port, resourcePackServerPort);
        }
    }
}
