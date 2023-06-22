package com.mcquest.core.model;

import org.jetbrains.annotations.ApiStatus;
import team.unnamed.hephaestus.Model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ModelManager {
    private final Collection<Model> models;

    @ApiStatus.Internal
    public ModelManager(Model[] models) {
        this.models = Arrays.asList(models);
    }

    public Collection<Model> getModels() {
        return Collections.unmodifiableCollection(models);
    }
}
