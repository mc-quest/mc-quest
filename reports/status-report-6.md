# Status Report 6

## Team Report

### Goals from Last Week

- Update all quests to use the new map provided by our artists
- Fully implement and playtest goblin quest

### Progress and Issue Report

- Nearly finished with goblin quests
- Completed wolf bite delight quest
- Broodmother quest started
- Implemented NPC loot tables, which are used to drop items that can be looted
  by the player

### Plans for Next Week

- Finish porting quests to the new map
- Finish goblin quests
- Make progress on Broodmother’s Lair quests
- Implement more player class skills

### Meeting Agenda

- No meeting on Thursday due to Thanksgiving
- On Tuesday:
  - Plan upcoming quests
  - Plan NPCs
  - Plan player stat progression balancing

## Contributions of Individual Team Members

### Goals from Last Week

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

### Progress and Issue Report

- Chien
  - Fixed pull request for Wolf Bite Delight
  - Pushed code for broodmother quest
- Christopher
  - Added 3 new upgrades for Fighter (Upgrades to bash and and self-heal)
  - Added 7 new upgrades to Rogue (Many for Wounding Slash, some for sneak, some
    to fan of   knives, some for dash, etc.)
  - Among the Rogue upgrades were ULTIMATE upgrades (such as bouncing blades,
    explosive poison, etc.)
  - Balance changes to skills (decreased damage of whirlwind and changed hitbox
    size, etc.)
- Connor
  - Fix Gradle instructions in README according to peer review feedback
  - Review pull requests for Wolf Bite Delight quests and skills
  - Implement NPC loot tables for quest items (in Chien’s `quests` branch)
- Eliot
  - Finished implementing the goblin quest and created a PR
  - Began playtesting goblin quest
  - Fixed all the wolf positions for the wolf quest
  - Spoke with the team to plan out an intermediate quest (between the goblin
    quest and the Broodmother’s Lair quest) located in the Ashen Tangle
- Kyle
  - Created stat modifiers which will activate/deactivate when a player
    equips/unequips an item
  - Chose to work on this instead of new mage skills; I’m moving that to next
    week.
- Vincent
  - Created new projectile class which abstracts the creation of linear
    projectile entities
  - Implemented this class in the action for mage fireballs

### Plans for Next Week

- Chien
  - brainstorm fetch quest ideas and implement
  - consider balancing NPCs damage and player stats
- Christopher
  - Balance skills
  - Obtain better skill icons
  - Add more skills to Fighter and Rogue (2-3 for each)
- Connor
  - Review rogue class pull requests
  - Review goblin quest pull request
  - Port previous quests to the new world
- Eliot
  - Implement the Ashen Tangle feature and quest
  - Playtest the goblin quest more, playtest the Ashen Tangle quest
- Kyle
  - Continue working on more mage skills
- Vincent
  - More thoroughly test and optimize projectile
  - Refactor `CharacterModel` class to generalize models for non-character usage
  - Maybe get started on inventory logic
