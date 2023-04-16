package com.mcquest.server.playerclass;

import com.mcquest.server.asset.Asset;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.ActiveSkillUseEvent;
import com.mcquest.server.event.EventEmitter;
import com.mcquest.server.resourcepack.ResourcePackUtility;
import com.mcquest.server.util.ItemStackUtility;
import com.mcquest.server.util.TextUtility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.texture.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ActiveSkill extends Skill {
    private static final int COOLDOWN_DIVISIONS = 16;

    private final double manaCost;
    private final Duration cooldown;
    private final EventEmitter<ActiveSkillUseEvent> onUse;
    int customModelDataStart;

    ActiveSkill(int id, String name, int level, @Nullable Integer prerequisiteId,
                Asset icon, String description, int skillTreeRow,
                int skillTreeColumn, double manaCost, Duration cooldown) {
        super(id, name, level, prerequisiteId, icon, description, skillTreeRow, skillTreeColumn);
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.onUse = new EventEmitter<>();
    }

    public double getManaCost() {
        return manaCost;
    }

    public Duration getCooldown() {
        return cooldown;
    }

    public EventEmitter<ActiveSkillUseEvent> onUse() {
        return onUse;
    }

    public Duration getCooldown(PlayerCharacter pc) {
        return pc.getSkillManager().getCooldown(this);
    }

    @Override
    ItemStack getSkillTreeItemStack(PlayerCharacter pc) {
        boolean isUnlocked = isUnlocked(pc);
        Material icon = isUnlocked ? SKILL_MATERIAL : Material.BARRIER;
        Component displayName = Component.text(getName(), NamedTextColor.YELLOW);
        List<TextComponent> lore = new ArrayList<>();
        lore.add(Component.text("Active Skill", NamedTextColor.GRAY));
        lore.add(Component.text("Mana Cost: " + (int) Math.round(manaCost), NamedTextColor.AQUA));
        int cooldownSeconds = cooldown.toSecondsPart();
        lore.add(Component.text("Cooldown: " + cooldownSeconds, NamedTextColor.GREEN));
        lore.add(Component.empty());
        lore.addAll(TextUtility.wordWrap(getDescription()));
        lore.add(Component.empty());
        if (isUnlocked) {
            lore.add(Component.text("Left-click to add to", NamedTextColor.GREEN));
            lore.add(Component.text("hotbar", NamedTextColor.GREEN));
        } else {
            Skill prerequisite = getPrerequisite();
            if (prerequisite != null && !prerequisite.isUnlocked(pc)) {
                lore.add(Component.text("Requires " + prerequisite.getName(), NamedTextColor.RED));
            } else if (pc.getLevel() < getLevel()) {
                lore.add(Component.text("Requires level " + getLevel(), NamedTextColor.RED));
            } else {
                lore.add(Component.text("Shift-click to", NamedTextColor.GREEN));
                lore.add(Component.text("unlock", NamedTextColor.GREEN));
            }
        }
        return ItemStackUtility.createItemStack(icon, displayName, lore);
    }

    ItemStack getHotbarItemStack() {
        Component displayName = Component.text(getName(), NamedTextColor.YELLOW);
        List<TextComponent> lore = new ArrayList<>();
        lore.add(Component.text("Active Skill", NamedTextColor.GRAY));
        lore.add(Component.text("Mana Cost: " + (int) Math.round(manaCost), NamedTextColor.AQUA));
        int cooldownSeconds = cooldown.toSecondsPart();
        lore.add(Component.text("Cooldown: " + cooldownSeconds, NamedTextColor.GREEN));
        lore.add(Component.empty());
        lore.addAll(TextUtility.wordWrap(getDescription()));
        return ItemStackUtility.createItemStack(SKILL_MATERIAL, displayName, lore)
                .withTag(PlayerClassManager.PLAYER_CLASS_ID_TAG, playerClass.getId())
                .withTag(PlayerClassManager.SKILL_ID_TAG, getId())
                .withMeta(builder -> builder.customModelData(customModelDataStart));
    }

    @Override
    @ApiStatus.Internal
    public int writeResources(FileTree tree, int customModelDataStart) {
        this.customModelDataStart = customModelDataStart;
        // Default texture.
        // TODO: move this
        String TEXTURE_NAMESPACE = "skill";
//        String defaultKeyValue = String.format("%d-%d", playerClass.getId(), getId());
//        Model model = Model.builder()
//                .key(null)
//                .textures(ModelTexture.builder()
//                        .layers()
//                        .build())
//                .build();
//         tree.write(defaultTexture);

        // Locked texture.

        // Cooldown textures.
        for (int i = 1; i <= COOLDOWN_DIVISIONS; i++) {
            String keyValue = String.format("%d-%d-%d", playerClass.getId(), getId(), i);
            Key key = Key.key(TEXTURE_NAMESPACE, keyValue);
            Texture texture = Texture.builder()
                    .key(key)
                    .data(Writable.bytes(cooldownTexture(i)))
                    .build();
            tree.write(texture);
        }

        return 2 + COOLDOWN_DIVISIONS;
    }

    private byte[] cooldownTexture(int cooldownDivision) {
        try {
            double thetaMax = (double) cooldownDivision / COOLDOWN_DIVISIONS * 2.0 * Math.PI;
            BufferedImage image = getIcon().readImage();
            double cx = image.getWidth() / 2.0;
            double cy = image.getHeight() / 2.0;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    double theta = Math.atan2(cy - y, x - cx) - Math.PI / 2.0;
                    if (theta < 0.0) {
                        theta = 2.0 * Math.PI + theta;
                    }
                    if (theta < thetaMax) {
                        int rgb = ResourcePackUtility.grayAndDarken(image.getRGB(x, y));
                        image.setRGB(x, y, rgb);
                    }
                }
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            return stream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
