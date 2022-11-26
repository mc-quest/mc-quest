package com.mcquest.server.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.constants.Items;
import com.mcquest.server.constants.PlayerClasses;
import com.mcquest.server.constants.Zones;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.ui.PlayerCharacterLogoutType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class Database {
    public Database() {
        // TODO: connect to database
    }

    public PlayerCharacterData getPlayerCharacterData(Player player) {
        // TODO: read from database.
        return PlayerCharacterData.create(PlayerClasses.FIGHTER, Instances.ELADRADOR,
                new Pos(0, 70, 0), Zones.OAKSHIRE, Items.ADVENTURERS_SWORD);
    }

    public void savePlayerCharacterData(PlayerCharacter pc, PlayerCharacterLogoutType logoutType) {
        // TODO: write to database.
        PlayerCharacterData data = PlayerCharacterData.save(pc);
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        System.out.println(gson.toJson(data));
    }
}
