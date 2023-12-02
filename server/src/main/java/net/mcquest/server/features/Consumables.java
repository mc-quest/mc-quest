package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ItemConsumeEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.server.constants.Items;

public class Consumables implements Feature {

    @Override
    public void hook(Mmorpg mmorpg) {
        Items.LESSER_HEALING_POTION.onConsume().subscribe(this::useLesserHealing);
        Items.LESSER_MANA_POTION.onConsume().subscribe(this::useLesserMana);
        Items.MINOR_MANA_POTION.onConsume().subscribe(this::useMinorMana);
    }

    private void useMinorMana(ItemConsumeEvent event) {
        event.getPlayerCharacter().addMana(4);
    }

    private void useLesserMana(ItemConsumeEvent event) {
        event.getPlayerCharacter().addMana(2);
    }

    private void useLesserHealing(ItemConsumeEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        pc.heal(pc, 2);
    }
}
