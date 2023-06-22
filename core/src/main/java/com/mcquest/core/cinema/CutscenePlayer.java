package com.mcquest.core.cinema;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.util.MathUtility;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.utils.time.Tick;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public class CutscenePlayer {
    private final PlayerCharacter pc;
    private Cutscene cutscene;
    private Pos pcPosition;
    private int shotIndex;
    private int keyFrameIndex;
    private Duration now;

    @ApiStatus.Internal
    public CutscenePlayer(PlayerCharacter pc) {
        this.pc = pc;
        cutscene = null;
    }

    public @Nullable Cutscene getPlayingCutscene() {
        return cutscene;
    }

    public void play(@NotNull Cutscene cutscene) {
        this.cutscene = cutscene;
        pcPosition = pc.getPosition();
        shotIndex = 0;
        keyFrameIndex = 0;
        now = Duration.ZERO;
        pc.getPlayer().setGameMode(GameMode.SPECTATOR);
    }

    public void stopCutscene() {
        if (cutscene == null) {
            return;
        }

        completeCutscene();
    }

    void tick() {
        if (cutscene == null) {
            return;
        }

        while (advance()) ;

        List<Shot> shots = cutscene.getShots();
        if (shotIndex == shots.size()) {
            completeCutscene();
            return;
        }

        Shot shot = shots.get(shotIndex);
        List<KeyFrame> keyFrames = shot.getKeyFrames();
        KeyFrame from = keyFrames.get(keyFrameIndex);
        KeyFrame to = keyFrames.get(keyFrameIndex + 1);
        Pos fromPosition = from.getPosition();
        Pos toPosition = to.getPosition();
        Duration fromTime = from.getTime();
        Duration toTime = to.getTime();

        double t = (double) now.minus(fromTime).toMillis()
                / toTime.minus(fromTime).toMillis();
        Pos newPosition = MathUtility.lerp(fromPosition, toPosition, t);
        pc.getPlayer().teleport(newPosition);

        now = now.plus(Tick.server(1));
    }

    private boolean advance() {
        List<Shot> shots = cutscene.getShots();
        if (shotIndex == shots.size()) {
            return false;
        }

        Shot shot = shots.get(shotIndex);
        List<KeyFrame> keyFrames = shot.getKeyFrames();
        if (keyFrameIndex >= keyFrames.size() - 1) {
            shotIndex++;
            keyFrameIndex = 0;
            now = Duration.ZERO;
            return true;
        }

        KeyFrame to = keyFrames.get(keyFrameIndex + 1);
        if (now.compareTo(to.getTime()) >= 0) {
            keyFrameIndex++;
            return true;
        } else {
            return false;
        }
    }

    private void completeCutscene() {
        cutscene = null;
        pc.setPosition(pcPosition);
        pc.getPlayer().setGameMode(GameMode.ADVENTURE);
    }
}
