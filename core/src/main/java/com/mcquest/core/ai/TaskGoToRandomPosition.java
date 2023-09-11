package com.mcquest.core.ai;

import com.mcquest.core.character.NonPlayerCharacter;
import com.mcquest.core.util.MathUtility;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

import java.util.ArrayList;
import java.util.List;

public class TaskGoToRandomPosition extends Task {
    private static final double ACCEPTANCE_RADIUS = 1.0;

    private final List<Vec> offsets;

    public TaskGoToRandomPosition(int radius) {
        offsets = getOffsets(radius);
    }

    @Override
    public void start(long time) {
        NonPlayerCharacter character = getCharacter();
        Navigator navigator = character.getNavigator();

        int remaining = offsets.size();
        while (remaining-- > 0) {
            int index = MathUtility.randomRange(0, offsets.size() - 1);
            Vec position = offsets.get(index);
            Pos target = character.getPosition().add(position);
            if (navigator.setPathTo(target)) {
                break;
            }
        }
    }

    @Override
    public BehaviorStatus update(long time) {
        NonPlayerCharacter character = getCharacter();
        Pos target = character.getNavigator().getPathPosition();

        if (target == null || character.getPosition().distanceSquared(target)
                <= ACCEPTANCE_RADIUS * ACCEPTANCE_RADIUS) {
            return BehaviorStatus.SUCCESS;
        }

        return BehaviorStatus.RUNNING;
    }

    @Override
    public void stop(long time) {
        getCharacter().getNavigator().setPathTo(null);
    }

    private static List<Vec> getOffsets(int radius) {
        List<Vec> offsets = new ArrayList<>();

        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    offsets.add(new Vec(x, y, z));
                }
            }
        }

        return offsets;
    }
}
