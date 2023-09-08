package com.mcquest.core.ai;

public class TaskPlayAnimation extends Task {
    private final String animation;

    public TaskPlayAnimation(String animation) {
        this.animation = animation;
    }

    @Override
    public void start(long time) {
        getCharacter().playAnimation(animation);
    }

    @Override
    public void stop(long time) {
        getCharacter().playAnimation(null);
    }
}
