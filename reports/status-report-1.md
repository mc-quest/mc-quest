# Status Report 1

## Team Report

### Goals from Last Week

- Make sure everyone has access to a Minecraft account.
- Get everyone's `mc-quest` repository set up.
- Complete project requirements milestone.
- Familiarize everyone with the basic architecture of `mc-quest`.

### Progress and Issue Report

- Got the repository set up and the server running on everyone's machine.
- Completed the project requirements milestone.
- Issue: The resource pack server is sending clients the wrong resource pack
  URL.

### Plans for Next Week

- Complete architecture and design milestone.
- Gain familiarity using Minestom and `core` API.
- Begin implementing the first few levels of gameplay.
- Work on first village: NPCs (Eliot) and quests (Chien).
- Work on Broodmother’s Lair Dungeon.
- Create 2 Skills for 2-3 Classes.
- Create 4-5 other Quests.

### Meeting Agenda

- Ensure Vinay is added to Git and Discord.
- Review sprint schedule.
- Discuss plans for next week.

## Contributions of Individual Team Members

### Goals from Last Week

- Christopher
  - Get a strong start on the rogue classes skills.
  - Write plans for different skills.
  - Figure out the existing code base and Minestom API.
- Connor
  - Introduce `core` APIs to team.
  - Added some useful behavior tree nodes for NPC AI:
    - `RandomSelector`: A `Composite` node that randomly selects one of its
      children to execute.
    - `SimpleParallel` A `Composite` node that executes its main child while
      also repeatedly executing a background behavior.

### Progress and Issue Report

- Chien
  - Adding use case 6 to project milestone: Character Selection.
  - Bought Minecraft JAVA edition, downloaded correct client, connected to
    project server successfully, explore current game (mechanics, world).
- Christopher
  - Made the frameworks for multiple rogue skills (some require refinement and
    further testing though) and have 1-2 working ones. There was a learning
    curve on how to code with the framework, so progress was hindered this week.
  - Wrote a TODO list of skills to keep track of what I am working on for the
    rest of my team. About half of it is irrelevant now as we had a team meeting
    to change things; however, that meeting only happened because of the
    discussion sparked by the TODO list.
  - Read through all of the Minestom documentation and most of the existing
    classes within our code base related to my work (predominantly relating to
    PlayerCharacter and SkillUseEvents). However, the existing Minestom
    documentation is very sparse, so the amount of useful information gathered
    was minimal.
- Connor
  - Enhancement: Refactored `core` to use `String` IDs rather than `int` IDs for
    quests, items, player classes, skills, music, etc. This will eliminate
    possible ID collisions and make IDs more descriptive.
- Eliot
  - Contributed Use Case 3: Completing a Quest to the Requirements Milestone.
    This was also useful in planning out what NPCs I am going to create and
    helped me get started thinking about it.
  - Worked on understanding the project structure and how to create NPCs &
    figured out how to add custom NPC skins, which I will create using one of
    many online Minecraft skin creation tools.
  - Issue 1: Had issues starting/running the server on my computer; managed to
    get it working as of 10/17.
  - Issue 2: Discovered that the resource pack is not loading as it should
    (needs fixing).
- Kyle
  - Wrote a document outlining some of the core mechanics behind damage,
    defenses, skills, and items.
  - Worked on the Requirements Milestone.
- Vincent
  - Added Gradle Fat Jar target.
  - Ran MCQuest server from a remote host for everyone to connect to.

### Plans for Next Week

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
