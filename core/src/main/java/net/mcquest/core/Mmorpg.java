package net.mcquest.core;

import net.mcquest.core.audio.AudioClip;
import net.mcquest.core.audio.AudioManager;
import net.mcquest.core.cartography.Map;
import net.mcquest.core.cartography.MapManager;
import net.mcquest.core.character.PlayerCharacterManager;
import net.mcquest.core.cinema.CutsceneManager;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.instance.InstanceManager;
import net.mcquest.core.item.Item;
import net.mcquest.core.item.ItemManager;
import net.mcquest.core.login.LoginManager;
import net.mcquest.core.loot.LootManager;
import net.mcquest.core.model.ModelManager;
import net.mcquest.core.mount.Mount;
import net.mcquest.core.mount.MountManager;
import net.mcquest.core.music.MusicManager;
import net.mcquest.core.music.Song;
import net.mcquest.core.object.ObjectManager;
import net.mcquest.core.particle.ParticleManager;
import net.mcquest.core.persistence.PersistenceService;
import net.mcquest.core.physics.PhysicsManager;
import net.mcquest.core.playerclass.PlayerClass;
import net.mcquest.core.playerclass.PlayerClassManager;
import net.mcquest.core.quest.Quest;
import net.mcquest.core.quest.QuestManager;
import net.mcquest.core.resourcepack.ResourcePackManager;
import net.mcquest.core.ui.InteractionHandler;
import net.mcquest.core.zone.Zone;
import net.mcquest.core.zone.ZoneManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.world.biomes.Biome;
import team.unnamed.hephaestus.Model;

public class Mmorpg {
    private final MinecraftServer server;
    private final String name;
    private final PlayerClassManager playerClassManager;
    private final ItemManager itemManager;
    private final QuestManager questManager;
    private final ZoneManager zoneManager;
    private final MusicManager musicManager;
    private final ModelManager modelManager;
    private final AudioManager audioManager;
    private final MapManager mapManager;
    private final MountManager mountManager;
    private final InstanceManager instanceManager;
    private final PlayerCharacterManager pcManager;
    private final ObjectManager objectManager;
    private final PhysicsManager physicsManager;
    private final ParticleManager particleManager;
    private final LootManager lootManager;
    private final CutsceneManager cutsceneManager;
    private final ResourcePackManager resourcePackManager;
    private final LoginManager loginManager;
    private final Feature[] features;
    private final PersistenceService persistenceService;

    private Mmorpg(Builder builder) {
        server = builder.server;
        name = builder.name;
        playerClassManager = new PlayerClassManager(this, builder.playerClasses);
        itemManager = new ItemManager(this, builder.items);
        questManager = new QuestManager(builder.quests);
        zoneManager = new ZoneManager(builder.zones);
        musicManager = new MusicManager(builder.music);
        modelManager = new ModelManager(builder.models);
        audioManager = new AudioManager(builder.audio);
        mapManager = new MapManager(this, builder.maps);
        mountManager = new MountManager(this, builder.mounts);
        instanceManager = new InstanceManager(builder.instances, builder.biomes);
        pcManager = new PlayerCharacterManager(this);
        objectManager = new ObjectManager(this);
        physicsManager = new PhysicsManager();
        particleManager = new ParticleManager();
        lootManager = new LootManager(this);
        cutsceneManager = new CutsceneManager(this);
        resourcePackManager = new ResourcePackManager(this);
        loginManager = new LoginManager(this);
        features = builder.features;
        persistenceService = builder.persistenceService;
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

    public MusicManager getMusicManager() {
        return musicManager;
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

    public PersistenceService getPersistenceService() {
        return persistenceService;
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
        PersistenceServiceStep features(Feature... features);
    }

    public interface PersistenceServiceStep {
        StartStep persistenceService(PersistenceService persistenceService);
    }

    public interface StartStep {
        void start(String address, int port, int resourcePackServerPort);
    }

    private static class Builder implements NameStep, PlayerClassesStep,
            ItemsStep, QuestsStep, ZonesStep, MusicStep, MapsStep, MountsStep,
            InstancesStep, BiomesStep, ModelsStep, AudioStep, FeaturesStep,
            PersistenceServiceStep, StartStep {
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
        private PersistenceService persistenceService;

        private Builder() {
            System.setProperty("minestom.chunk-view-distance", String.valueOf(10));
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
        public PersistenceServiceStep features(Feature... features) {
            this.features = features.clone();
            return this;
        }

        @Override
        public StartStep persistenceService(PersistenceService persistenceService) {
            this.persistenceService = persistenceService;
            return this;
        }

        @Override
        public void start(String address, int port, int resourcePackServerPort) {
            Mmorpg mmorpg = new Mmorpg(this);
            mmorpg.start(address, port, resourcePackServerPort);
        }
    }
}
