# Status Report 7

## Team Report

### Goals from Last Week

- Finish porting quests to the new map
- Finish goblin quests
- Make progress on Broodmother’s Lair quests
- Implement more player class skills

### Progress and Issue Report

- Finished porting quests and locations to the new map
- Made progress on King’s Deathrow and Broodmother’s Lair quests
- Made progress on rogue and fighter player classes

### Plans for Next Week

- Healing potions
- Mana potions
- Fix player character persistence issues (quests, skills, etc.)
- Fix inventory event issues that occur when the player logs out through the
  menu
- Place objects in world for King’s Deathrow quests

### Meeting Agenda

- Meet this weekend to discuss final milestone and demo

## Contributions of Individual Team Members

### Goals from Last Week

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

### Progress and Issue Report

- Chien
  - Made progress in overall completion of Broodmother Dungeon
- Christopher
  - Added 2 new passive skills to Rogue and Fighter.
  - Balanced Fighter and Rogue hitboxes (whirlwind, etc.) amongst other things
  - Placed orders with artists for new skill icons
- Connor
  - Ported Wolf Bite Delight quest to new map
  - Ported Canine Carnage to new map
  - Ported Prowlwood and Prowlwood outpost locations to new map
  - Reviewed rogue PRs
  - Reviewed fighter PRs 
- Eliot
  - Have started Ashen Tangle Quest but not fully implemented yet
  - Finally got map updated, fixed all positions
  - Began playtesting goblin quest
- Kyle
  - Create 2 new mage active skills: frozen orb and flame wall.
- Vincent
  - Create and implement ProjectileModel class to abstract entity logic for
    projectiles
  - Various projectile refactorings

### Plans for Next Week

- Chien
  - Finish all implementations of brood mother dungeon
  - Create more quests
  - Finalize quest line (prereqs)
- Christopher
  - Work on Implementing more items into MCQuest (stretch goal). Probably 2-5
    new ones depending on their complexity (new weapons, potions, armor, etc.)
- Connor
  - Fix inventory event bugs that occur when the player logs out using the menu
  - Fix player character persistence
  - Make sure consumable items are working correctly
- Eliot
  - Finish and playtest all quests in preparation for final demo
  - Plan out my part of the presentation
- Kyle
  - Get all branches for mage skills merged into main.
- Vincent
  - Implement cooldown time for primary attack
