# Status Report 2

## Team Report

### Goals from Last Week

- Complete architecture and design milestone.
- Gain familiarity using Minestom and `core` API.
- Begin implementing the first few levels of gameplay.
- Work on first village: NPCs (Eliot) and quests (Chien).
- Work on Broodmother’s Lair Dungeon.
- Create 2 Skills for 2-3 Classes.
- Create 4-5 other Quests.

### Progress and Issue Report

- The team is becoming familiar with Minestom and the MCQuest `core` libraries
- The world environment for the first 5 levels is mostly complete and ready to
  be populated
- Our team initially had some disagreement about the skill and player class APIs
  implemented in `core`, but they have been resolved. In the future, we may
  switch to a tick-based skill system rather than a simple callback-based skill
  system, but callbacks seem to be sufficient for now.

### Plans for Next Week

- Meet this weekend to discuss non-player character development, including
  artificial intelligence
- Meet this weekend to discuss quests, including item collection objectives and
  non-player character slay objectives
- Finish player character creation, selection, and persistence with a mock
  database
- Begin implementing stat system
- Continue working on gameplay levels 1-5

### Meeting Agenda

- Discuss stat system (Kyle, Connor)
- Discuss non-player character and quest development (Connor, Chien, Eliot)

## Contributions of Individual Team Members

### Goals from Last Week

- Chien
  - Plan out several versions of an overarching story based on current assets.
  - Ask for feedback from team members and incorporate feedback.
  - Let team vote on story route.
  - Create first quest (tutorial).
  - Create additional 3-5 quests, working with relevant team members (npcs).
- Christopher
  - Finish the framework for existing planned Fighter classes
  - Fix Rogue Skills where they are broken
  - Merge with rest of git project and fix any changed classes in my code
- Connor
  - Add features in `core` needed by `server`:
    - Invisibility (will need to modify behavior tree nodes that manage
      targeting)
    - Add `EventEmitter<PlayerCharacterMoveEvent> PlayerCharacter#onMove()`
      method for movement-based skills, such as Overhead Strike.
  - Implement character creation and selection, to be completed by 10/31.
- Eliot
  - Create and flesh out an NPC to introduce the tutorial quest
  - Create 3-6 other NPCs to introduce the ~10 other quests
  - Write 5-10 more lines of randomized dialogue for the basic Oakshire
    Villagers (which they will say when approached by a player)
- Kyle
  - Design an implementation for storing character stats for both players and
    enemies.
  - Design an implementation for using skills and dealing damage.
- Vincent
  - Explore deployment options.

### Progress and Issue Report

- Chien
  - Looked through assets and started forming ideas for questline
- Christopher
  - Added 2 new skills for the Fighter class (taunt and armor up), fixed the
    sneak skill for rogue, and began work on the fan of knives skill for Rogue
- Connor
  - Developed the basic GUI and mechanisms for player character selection and
    persistence
  - Fixed a bug where passive skills weren’t being written to the resource pack
    correctly
  - Improved the `core` character APIs in response to the shortcomings
    identified by Christopher while developing rogue and fighter skills (added
    character invisibility, velocity, `PlayerCharacter#onMove()` event emitter,
    and `Character#isOnGround()` method)
  - Made stylistic improvements to behavior tree API
  - Finalized Gradle fat Jar build target with Vincent to be used for deployment
  - Researched automatic map generation techniques (we will use the Xaeros World
    Map mod)
  - Added architecture and software design documentation to living document
- Eliot
  - Began work on Developing two tutorial NPCs, communicated with other devs to
    plan out tutorial quest
  - Worked on planning out dialogue for Villagers
  - Thought out and wrote 4/5 Risks to the Risks section for the Architecture
    and Design Milestone
- Kyle
  - Gained more familiarity with relevant classes for skills, stats, and damage.
  - Discussed with Connor what types of stats (such as damage modifiers) we want
    to include in MCQuest.
  - Decided upon the general implementation strategy for storing player stats
    and calculating damage.

### Plans for Next Week

- Chien
  - Meet to discuss quest development
  - Ask for feedback from team members and incorporate feedback.
  - Let team vote on story route.
  - Create first quest (tutorial).
  - Create additional 3-5 quests, working with relevant team members (npcs).
- Christopher
  - Finish Fan of Knives skill, test all the skills to make sure there are no
    bugs, fix fleet of foot skill, implement skills that don’t require being
    used to take effect
- Connor
  - Gather some minimap images with artists
  - Complete player character creation, selection, and persistence, with an
    emphasis on character creation
  - Work on `core` stat system with Kyle
- Eliot
  - Figure out how the actual quest giving/quest completion will work
  - Finish tutorial quest NPCs & work with Chien to make sure tutorial quest
    works
  - Finish creating around 10 other NPCs to introduce other quests
- Kyle
  - Write the code for storing player stats and calculating damage using those
    stats.
  - To start, player stats will probably have hard coded values. If I have time
    this week, I will have them dynamically update based on events such as
    equipping an item.
