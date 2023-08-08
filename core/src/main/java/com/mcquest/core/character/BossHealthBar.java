package com.mcquest.core.character;

import net.kyori.adventure.bossbar.BossBar;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.bossbar.BossBarManager;

import java.util.Collections;

public class BossHealthBar {
    private final Character boss;
    private final BossBar bossBar;

    public BossHealthBar(Character boss) {
        this.boss = boss;
        bossBar = BossBar.bossBar(boss.nameText(Attitude.HOSTILE), progress(),
                BossBar.Color.RED, BossBar.Overlay.NOTCHED_12, Collections.emptySet());
    }

    public void addViewer(PlayerCharacter pc) {
        pc.getPlayer().showBossBar(bossBar);
    }

    public void removeViewer(PlayerCharacter pc) {
        pc.getPlayer().hideBossBar(bossBar);
        BossBarManager bossBarManager = MinecraftServer.getBossBarManager();
        if (bossBarManager.getBossBarViewers(bossBar).isEmpty()) {
            bossBarManager.destroyBossBar(bossBar);
        }
    }

    void updateText() {
        bossBar.name(boss.nameText(Attitude.HOSTILE));
    }

    void updateHealth() {
        bossBar.progress(progress());
    }

    private float progress() {
        return (float) (boss.getHealth() / boss.getMaxHealth());
    }
}
