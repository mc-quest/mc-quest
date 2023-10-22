package net.mcquest.core.event;

import net.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;

public class PlayerCharacterCreateEvent implements Event {
    public PlayerCharacterCreateEvent() {
        instance = null;
        position = null;
    }

    public Instance getInstance() {
        return instance;
    }

    public Pos getPosition() {
        return position;
    }
}
