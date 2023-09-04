package com.mcquest.core.cinema;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.entity.EntityHuman;
import com.mcquest.core.item.ArmorItem;
import com.mcquest.core.item.ArmorSlot;
import com.mcquest.core.item.PlayerCharacterInventory;
import com.mcquest.core.item.Weapon;
import com.mcquest.core.util.MathUtility;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
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
    private EntityHuman placeholder;
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
        if (this.cutscene != null) {
            completeCutscene();
        }

        this.cutscene = cutscene;
        pcPosition = pc.getPosition();
        placeholder = createPlaceholder();
        placeholder.setInstance(pc.getInstance(), pcPosition);
        shotIndex = 0;
        keyFrameIndex = 0;
        now = Duration.ZERO;
        pc.getEntity().setGameMode(GameMode.SPECTATOR);
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

        Pos fromPosition;
        Duration fromTime;
        if (keyFrameIndex == 0) {
            fromPosition = shot.getStartPosition();
            fromTime = Duration.ZERO;
        } else {
            KeyFrame from = keyFrames.get(keyFrameIndex - 1);
            fromPosition = from.getPosition();
            fromTime = from.getTime();
        }

        KeyFrame to = keyFrames.get(keyFrameIndex);
        Pos toPosition = to.getPosition();
        Duration toTime = to.getTime();

        double t = (double) now.minus(fromTime).toMillis()
                / toTime.minus(fromTime).toMillis();
        double s = to.getInterpolation().interpolate(t);

        Pos newPosition = MathUtility.lerp(fromPosition, toPosition, s);
        pc.getEntity().teleport(newPosition);

        now = now.plus(Tick.server(1));
    }

    private EntityHuman createPlaceholder() {
        Player player = pc.getEntity();
        PlayerSkin skin = PlayerSkin.fromUuid(player.getUuid().toString());
        EntityHuman placeholder = new EntityHuman(skin);

        PlayerCharacterInventory inventory = pc.getInventory();
        Weapon weapon = inventory.getWeapon();
        ArmorItem feet = inventory.getArmor(ArmorSlot.FEET);
        ArmorItem legs = inventory.getArmor(ArmorSlot.LEGS);
        ArmorItem chest = inventory.getArmor(ArmorSlot.CHEST);
        ArmorItem head = inventory.getArmor(ArmorSlot.HEAD);

        if (weapon != null)
            placeholder.setEquipment(EquipmentSlot.MAIN_HAND, weapon.getItemStack());
        if (feet != null)
            placeholder.setEquipment(EquipmentSlot.BOOTS, feet.getItemStack());
        if (legs != null)
            placeholder.setEquipment(EquipmentSlot.LEGGINGS, legs.getItemStack());
        if (chest != null)
            placeholder.setEquipment(EquipmentSlot.CHESTPLATE, chest.getItemStack());
        if (head != null)
            placeholder.setEquipment(EquipmentSlot.HELMET, head.getItemStack());

        return placeholder;
    }

    private boolean advance() {
        List<Shot> shots = cutscene.getShots();
        if (shotIndex == shots.size()) {
            return false;
        }

        Shot shot = shots.get(shotIndex);
        List<KeyFrame> keyFrames = shot.getKeyFrames();
        if (keyFrameIndex == keyFrames.size()) {
            shotIndex++;
            keyFrameIndex = 0;
            now = Duration.ZERO;
            return true;
        }

        KeyFrame to = keyFrames.get(keyFrameIndex);
        if (now.compareTo(to.getTime()) >= 0) {
            keyFrameIndex++;
            return true;
        }

        return false;
    }

    private void completeCutscene() {
        cutscene = null;
        placeholder.remove();
        pc.setPosition(pcPosition);
        pc.getEntity().setGameMode(GameMode.ADVENTURE);
    }
}
