package com.mcquest.server.quest;

/**
 * A QuestStatus represents a PlayerCharacter's status in a Quest.
 */
public enum QuestStatus {
    /**
     * The PlayerCharacter has not started the Quest.
     */
    NOT_STARTED,
    /**
     * The PlayerCharacter has started the Quest, but has not completed it.
     */
    IN_PROGRESS,
    /**
     * The PlayerCharacter has completed the Quest.
     */
    COMPLETED
}
