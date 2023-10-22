package net.mcquest.core.event;

import net.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;

public class PlayerCharacterCreateEvent implements Event {
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public record Result(Instance instance, Pos position) {
    }
}
