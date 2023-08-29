package com.mcquest.core.model;

import com.mcquest.core.instance.Instance;
import com.mcquest.core.object.Object;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.minestom.ModelEntity;

public final class ModelObject extends Object {
    private final Model model;
    private String animation;
    private ModelEntity entity;

    public ModelObject(Instance instance, Pos position, Model model) {
        super(instance, position, boundingBox(model));
        this.animation = null;
        this.model = model;
    }

    private static Vec boundingBox(Model model) {
        Vector2Float boundingBox = model.boundingBox();
        return new Vec(boundingBox.x(), boundingBox.y(), boundingBox.x());
    }

    public void playAnimation(@Nullable String animation) {
        this.animation = animation;
        if (isSpawned()) {
            if (animation == null) {
                entity.animationController().clearQueue();
            } else {
                entity.playAnimation(animation);
            }
        }
    }

    @Override
    public void setInstance(Instance instance, Pos position) {
        super.setInstance(instance, position);
        if (isSpawned()) {
            entity.setInstance(instance, position);
        }
    }

    @Override
    public void setPosition(Pos position) {
        super.setPosition(position);
        if (isSpawned()) {
            entity.teleport(position);
        }
    }

    @Override
    protected void spawn() {
        super.spawn();
        entity = new ModelEntity(model);
        entity.setNoGravity(true);
        entity.setInstance(getInstance(), getPosition());
        if (animation != null) {
            entity.playAnimation(animation);
        }
    }

    @Override
    protected void despawn() {
        super.despawn();
        entity.remove();
    }
}
