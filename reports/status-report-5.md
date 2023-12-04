# Status Report 5

## Team Report

### Goals from Last Week

- Continue adding early level quests
- Work on Checkpoint Milestone

### Progress and Issue Report

- The demo did not go well due to Zoom’s low screen share framerates. We will
  likely pre-record gameplay for the final demo to address this.
- The goblin quest is mostly finished but has a few issues including bugs and
  missing positions; needs to be fleshed out and playtested
- Wolf Bite Delight quest done and broodmother quest in progress

### Plans for Next Week

- Update all quests to use the new map provided by our artists
- Fully implement and playtest goblin quest

### Meeting Agenda

- Plan out more quests/features
- Discuss NPC implementation
- Review pull requests

## Contributions of Individual Team Members

### Goals from Last Week

- Eliot
  - Create/continue working on the goblin NPCs for the goblin quest
  - Create the feature for the goblin quest
  - Plan out the next few quests to be worked on
- Christopher
  - Refine existing skills
  - Begin implementing buffs to skills (skill tree) that can be unlocked (such
    as fan of knives throwing more daggers, etc.). Get at least 2 of those done.
- Connor
  - Implement auto attack cooldowns
  - Fix active selector behavior tree node bug
  - Work on quests and NPCs
- Kyle
  - Create the necessary data structures for storing character stats
- Chien
  - Finish working with others to finish Wolf Bite Delight
  - Plan out more quests to complete with team members

### Progress and Issue Report

- Chien
  - Finish Wolf Bite Delight
  - Started broodmother quest
- Christopher
  - Added 6 passive skill upgrades for Fighter (2 for whirlwind, charge, and
    overhead-strike)
  - Fixed a bug with Wounding Slash as well as the onUnlock() listener
  - Added 4 Passive skills for Rogue (1 for Sneak, 1 for Fan of Knives, 2 for
    Backstab
  - Currently no new big issues or bugs known for classes
- Connor
  - Implemented passive skills in the skill tree
  - Implemented `READY_TO_START` quest markers, indicating on the map that a
    quest is ready to begin
  - Created issue templates
- Eliot
  - Created three NPCs for the goblin quest
  - Fully implemented 2/3 of these NPCs
  - Issue 1: having some issues debugging the Dreadfang NPC due to the fact that
    she has complex behavior including both InteractionSequences and combat
    mechanics
  - Issue 2: still need to plan & implement a quest giver NPC for this quest
  - Created and mostly implemented the KingsDeathRow.java feature
  - Issue 3: still need to get positions for this feature
  - Found and imported music for the KINGS_DEATH_ROW zone and also the two
    goblin boss battles
- Kyle
  - Created a mage skill, chain lightning

### Plans for Next Week

- Chien
  - Finish broodmother quest and more quests
- Christopher
  - Implement more passive skills. (Likely 4-6 more for fighter and 6-8 for
    Rogue)
   - Implement “ultimate upgrades” that require the entire skill tree of an
     ability to unlock (likely 1-2)
- Connor
  - Review pull requests for Wolf Bite Delight (by Chien), skills (by
    Christopher), and goblin-related quests (by Eliot)
  - Port previous quests to the new map, which will consist of updating a lot of
    positions
- Eliot
  - Finish implementing the goblin quest
  - Extensively playtest the goblin quest
  - Speak with the team to determine the next quest
  - Begin work on the next quest, finish as much as possible
- Kyle
  - Continue working on more mage skills
