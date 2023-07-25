package com.mcquest.core;

import com.mcquest.core.audio.Song;
import com.mcquest.core.cartography.Map;
import com.mcquest.core.cartography.MapManager;
import com.mcquest.core.character.CharacterEntityManager;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.character.PlayerCharacterManager;
import com.mcquest.core.cinema.CutsceneManager;
import com.mcquest.core.feature.Feature;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.instance.InstanceManager;
import com.mcquest.core.loot.LootManager;
import com.mcquest.core.model.ModelManager;
import com.mcquest.core.mount.Mount;
import com.mcquest.core.mount.MountManager;
import com.mcquest.core.object.ObjectManager;
import com.mcquest.core.particle.ParticleManager;
import com.mcquest.core.persistence.PlayerCharacterData;
import com.mcquest.core.physics.PhysicsManager;
import com.mcquest.core.playerclass.PlayerClass;
import com.mcquest.core.playerclass.PlayerClassManager;
import com.mcquest.core.quest.Quest;
import com.mcquest.core.quest.QuestManager;
import com.mcquest.core.resourcepack.ResourcePackManager;
import com.mcquest.core.zone.Zone;
import com.mcquest.core.zone.ZoneManager;
import com.mcquest.core.audio.AudioClip;
import com.mcquest.core.audio.AudioManager;
import com.mcquest.core.item.Item;
import com.mcquest.core.item.ItemManager;
import com.mcquest.core.ui.InteractionHandler;
import com.mcquest.core.ui.PlayerCharacterLogoutType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.world.biomes.Biome;
import team.unnamed.hephaestus.Model;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class Mmorpg {
    private final MinecraftServer server;
    private final String name;
    private final PlayerClassManager playerClassManager;
    private final ItemManager itemManager;
    private final QuestManager questManager;
    private final ZoneManager zoneManager;
    private final ModelManager modelManager;
    private final AudioManager audioManager;
    private final MapManager mapManager;
    private final MountManager mountManager;
    private final InstanceManager instanceManager;
    private final PlayerCharacterManager pcManager;
    private final CharacterEntityManager characterEntityManager;
    private final ObjectManager objectManager;
    private final PhysicsManager physicsManager;
    private final ParticleManager particleManager;
    private final LootManager lootManager;
    private final CutsceneManager cutsceneManager;
    private final ResourcePackManager resourcePackManager;
    private final Feature[] features;

    private Mmorpg(Builder builder) {
        server = builder.server;
        name = builder.name;
        playerClassManager = new PlayerClassManager(this, builder.playerClasses);
        itemManager = new ItemManager(this, builder.items);
        questManager = new QuestManager(builder.quests);
        zoneManager = new ZoneManager(builder.zones);
        modelManager = new ModelManager(builder.models);
        audioManager = new AudioManager(builder.audio, builder.music);
        mapManager = new MapManager(this, builder.maps);
        mountManager = new MountManager(this, builder.mounts);
        instanceManager = new InstanceManager(builder.instances, builder.biomes);
        pcManager = new PlayerCharacterManager(
                this,
                builder.pcDataProvider,
                builder.pcLogoutHandler
        );
        characterEntityManager = new CharacterEntityManager();
        objectManager = new ObjectManager(this);
        physicsManager = new PhysicsManager();
        particleManager = new ParticleManager();
        lootManager = new LootManager(this);
        cutsceneManager = new CutsceneManager(this);
        resourcePackManager = new ResourcePackManager(this);
        features = builder.features;
        InteractionHandler interactionHandler = new InteractionHandler(this);
        interactionHandler.registerListeners();
    }

    private void start(String address, int port, int resourcePackServerPort) {
        MojangAuth.init();
        for (Feature feature : features) {
            feature.hook(this);
        }
        resourcePackManager.startServer(address, resourcePackServerPort);
        server.start(address, port);
    }

    public String getName() {
        return name;
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

    public ModelManager getModelManager() {
        return modelManager;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public MountManager getMountManager() {
        return mountManager;
    }

    public InstanceManager getInstanceManager() {
        return instanceManager;
    }

    public PlayerCharacterManager getPlayerCharacterManager() {
        return pcManager;
    }

    public CharacterEntityManager getCharacterEntityManager() {
        return characterEntityManager;
    }

    public ObjectManager getObjectManager() {
        return objectManager;
    }

    public PhysicsManager getPhysicsManager() {
        return physicsManager;
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    public CutsceneManager getCutsceneManager() {
        return cutsceneManager;
    }

    public ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    public GlobalEventHandler getGlobalEventHandler() {
        return MinecraftServer.getGlobalEventHandler();
    }

    public SchedulerManager getSchedulerManager() {
        return MinecraftServer.getSchedulerManager();
    }

    public static NameStep builder() {
        return new Builder();
    }

    public interface NameStep {
        PlayerClassesStep name(String name);
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
        MountsStep maps(Map... maps);
    }

    public interface MountsStep {
        InstancesStep mounts(Mount... mounts);
    }

    public interface InstancesStep {
        BiomesStep instances(Instance... instances);
    }

    public interface BiomesStep {
        ModelsStep biomes(Biome... biomes);
    }

    public interface ModelsStep {
        AudioStep models(Model... models);
    }

    public interface AudioStep {
        FeaturesStep audio(AudioClip... audio);
    }

    public interface FeaturesStep {
        PlayerCharacterDataProviderStep features(Feature... features);
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

    private static class Builder implements NameStep, PlayerClassesStep,
            ItemsStep, QuestsStep, ZonesStep, MusicStep, MapsStep, MountsStep,
            InstancesStep, BiomesStep, ModelsStep, AudioStep, FeaturesStep,
            PlayerCharacterDataProviderStep, PlayerCharacterLogoutHandlerStep,
            StartStep {
        private final MinecraftServer server;
        private String name;
        private PlayerClass[] playerClasses;
        private Item[] items;
        private Quest[] quests;
        private Zone[] zones;
        private Song[] music;
        private Map[] maps;
        private Mount[] mounts;
        private Biome[] biomes;
        private Instance[] instances;
        private Model[] models;
        private AudioClip[] audio;
        private Feature[] features;
        private Function<Player, PlayerCharacterData> pcDataProvider;
        private BiConsumer<PlayerCharacter, PlayerCharacterLogoutType> pcLogoutHandler;

        private Builder() {
            server = MinecraftServer.init();
        }

        @Override
        public PlayerClassesStep name(String name) {
            this.name = name;
            return this;
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
        public MountsStep maps(Map... maps) {
            this.maps = maps.clone();
            return this;
        }

        @Override
        public InstancesStep mounts(Mount... mounts) {
            this.mounts = mounts;
            return this;
        }

        @Override
        public BiomesStep instances(Instance... instances) {
            this.instances = instances.clone();
            return this;
        }

        @Override
        public ModelsStep biomes(Biome... biomes) {
            this.biomes = biomes;
            return this;
        }

        @Override
        public AudioStep models(Model... models) {
            this.models = models.clone();
            return this;
        }

        @Override
        public FeaturesStep audio(AudioClip... audio) {
            this.audio = audio.clone();
            return this;
        }

        @Override
        public PlayerCharacterDataProviderStep features(Feature... features) {
            this.features = features.clone();
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
