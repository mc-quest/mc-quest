package net.mcquest.server.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.constants.Items;
import net.mcquest.server.constants.PlayerClasses;
import net.mcquest.server.constants.Zones;
import net.mcquest.core.persistence.PlayerCharacterData;
import net.mcquest.core.ui.PlayerCharacterLogoutType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class Database {
    public Database() {
        // TODO: connect to database
    }

    public PlayerCharacterData getPlayerCharacterData(Player player) {
        // TODO: read from database.

        return PlayerCharacterData.create(PlayerClasses.ROGUE, Instances.ELADRADOR,
                new Pos(0, 70, 0), Zones.OAKSHIRE, Items.ADVENTURERS_SWORD);
    }

    public void savePlayerCharacterData(PlayerCharacter pc, PlayerCharacterLogoutType logoutType) {
        // TODO: write to database.
        PlayerCharacterData data = PlayerCharacterData.save(pc);
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        System.out.println(gson.toJson(data));
    }
}
