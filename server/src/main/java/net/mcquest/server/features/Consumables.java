package net.mcquest.server.features;

import net.kyori.adventure.sound.Sound;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ItemConsumeEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.server.constants.Items;
import net.minestom.server.sound.SoundEvent;

public class Consumables implements Feature {
    @Override
    public void hook(Mmorpg mmorpg) {
        Items.MINOR_HEALING_POTION.onConsume().subscribe(this::useMinorHealingPotion);
        Items.LESSER_HEALING_POTION.onConsume().subscribe(this::useLesserHealingPotion);
        Items.MINOR_MANA_POTION.onConsume().subscribe(this::useMinorManaPotion);
        Items.LESSER_MANA_POTION.onConsume().subscribe(this::useLesserManaPotion);
    }

    private void useMinorHealingPotion(ItemConsumeEvent event) {
        useHealingPotion(event.getPlayerCharacter(), 25.0);
    }

    private void useLesserHealingPotion(ItemConsumeEvent event) {
        useHealingPotion(event.getPlayerCharacter(), 50.0);
    }

    private void useMinorManaPotion(ItemConsumeEvent event) {
        useManaPotion(event.getPlayerCharacter(), 25.0);
    }

    private void useLesserManaPotion(ItemConsumeEvent event) {
        useManaPotion(event.getPlayerCharacter(), 50.0);
    }

    private void useHealingPotion(PlayerCharacter pc, double amount) {
        potionUseSound(pc);
        pc.heal(pc, amount);
    }

    private void useManaPotion(PlayerCharacter pc, double amount) {
        potionUseSound(pc);
        pc.addMana(amount);
    }

    private void potionUseSound(PlayerCharacter pc) {
        Instance instance = pc.getInstance();
        instance.playSound(Sound.sound(
                SoundEvent.ENTITY_SPLASH_POTION_BREAK,
                Sound.Source.PLAYER,
                0.5f,
                1f
        ), pc.getPosition());
    }
}
