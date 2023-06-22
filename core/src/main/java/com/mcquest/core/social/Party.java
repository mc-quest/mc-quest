package com.mcquest.core.social;

import com.mcquest.core.character.PlayerCharacter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Party {
    private final List<PlayerCharacter> members;
    private PlayerCharacter leader;

    Party(PlayerCharacter leader) {
        members = new ArrayList<>();
        members.add(leader);
        this.leader = leader;
    }

    public PlayerCharacter getLeader() {
        return leader;
    }

    public List<PlayerCharacter> getMembers() {
        return Collections.unmodifiableList(members);
    }
}
