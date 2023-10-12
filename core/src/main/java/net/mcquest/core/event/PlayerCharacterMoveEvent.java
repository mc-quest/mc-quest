package net.mcquest.core.event;

import net.mcquest.core.character.PlayerCharacter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerCharacterMoveEvent implements Event {
    private final PlayerCharacter pc;
    private final PlayerMoveEvent playerMoveEvent;

    public PlayerCharacterMoveEvent(PlayerCharacter pc, PlayerMoveEvent playerMoveEvent) {
        this.pc = pc;
        this.playerMoveEvent = playerMoveEvent;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    @NotNull
    public Pos getNewPosition() {
        return playerMoveEvent.getNewPosition();
    }

    public void setNewPosition(@NotNull Pos newPosition) {
        playerMoveEvent.setNewPosition(newPosition);
    }

    public boolean isOnGround() {
        return playerMoveEvent.isOnGround();
    }

    public boolean isCancelled() {
        return playerMoveEvent.isCancelled();
    }

    public void setCancelled(boolean cancel) {
        playerMoveEvent.setCancelled(cancel);
    }
}
