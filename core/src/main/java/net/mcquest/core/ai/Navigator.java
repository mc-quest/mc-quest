package net.mcquest.core.ai;

import net.mcquest.core.character.NonPlayerCharacter;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.pathfinding.PFPathingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class Navigator {
    private final NonPlayerCharacter character;

    @ApiStatus.Internal
    public Navigator(NonPlayerCharacter character) {
        this.character = character;
    }

    public @Nullable Pos getPathPosition() {
        Point position = navigator().getPathPosition();
        return position == null ? null : Pos.fromPoint(position);
    }

    public boolean setPathTo(Pos position) {
        return navigator().setPathTo(position);
    }

    public void jump(double height) {
        navigator().jump((float) height);
    }

    public boolean isAvian() {
        return pathingEntity().isAvian();
    }

    public void setAvian(boolean avian) {
        pathingEntity().setAvian(avian);
    }

    public boolean isSwimmer() {
        return pathingEntity().isSwimmer();
    }

    public void setSwimmer(boolean swimmer) {
        pathingEntity().setSwimmer(swimmer);
    }

    public boolean isAquaphobic() {
        return pathingEntity().isAquaphobic();
    }

    public void setAquaphobic(boolean aquaphobic) {
        pathingEntity().setAquaphobic(aquaphobic);
    }

    public boolean avoidsDoorways() {
        return pathingEntity().isAvoidsDoorways();
    }

    public void setAvoidsDoorways(boolean avoidsDoorways) {
        pathingEntity().setAvoidsDoorways(avoidsDoorways);
    }

    public boolean isClimber() {
        return pathingEntity().isClimber();
    }

    public void setClimber(boolean climber) {
        pathingEntity().setClimber(climber);
    }

    private net.minestom.server.entity.pathfinding.Navigator navigator() {
        return character.getEntity().getNavigator();
    }

    private PFPathingEntity pathingEntity() {
        return navigator().getPathingEntity();
    }
}
