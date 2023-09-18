package com.mcquest.core.model;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.minestomce.ModelClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ModelManager {
    private final Collection<Model> models;

    @ApiStatus.Internal
    public ModelManager(Model[] models) {
        this.models = new ArrayList<>();
        this.models.addAll(Arrays.asList(CoreModels.all()));
        this.models.addAll(Arrays.asList(models));

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        ModelClickListener.register(eventHandler);
    }

    public Collection<Model> getModels() {
        return Collections.unmodifiableCollection(models);
    }
}
