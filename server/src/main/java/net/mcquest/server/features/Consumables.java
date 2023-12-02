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
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        Items.LESSER_HEALING_POTION.onConsume().subscribe(this::useLesserHealing);
        Items.LESSER_MANA_POTION.onConsume().subscribe(this::useLesserMana);
        Items.MINOR_MANA_POTION.onConsume().subscribe(this::useMinorMana);
    }

    private void useMinorMana(ItemConsumeEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        potionUseSound(pc);
        pc.addMana(25);
    }

    private void useLesserMana(ItemConsumeEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        potionUseSound(pc);
        pc.addMana(50);
    }

    private void useLesserHealing(ItemConsumeEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        potionUseSound(pc);
        pc.heal(pc, 50);
    }

    // Takes in a player character and makes a potion splash effect
    // sound at their location
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
