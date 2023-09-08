package com.mcquest.core.ai;

import com.mcquest.core.character.NonPlayerCharacter;

public class BehaviorTree {
    private final NonPlayerCharacter character;
    private final Blackboard blackboard;
    private Behavior root;

    public BehaviorTree(NonPlayerCharacter character) {
        this.character = character;
        this.blackboard = new Blackboard();
    }

    public void setRoot(Behavior root) {
        this.root = root;
        root.initialize(this);
    }

    public void tick(long time) {
        if (root != null) {
            root.tick(time);
        }
    }

    NonPlayerCharacter getCharacter() {
        return character;
    }

    Blackboard getBlackboard() {
        return blackboard;
    }
}
