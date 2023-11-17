# Status Report 3

## Team Report

### Goals from Last Week

- Meet over the weekend to discuss non-player character development, including
  artificial intelligence
- Meet over the weekend weekend to discuss quests, including item collection
  objectives and non-player character slay objectives
- Finish player character creation, selection, and persistence with a mock
  database
- Begin implementing stat system
- Continue working on gameplay levels 1-5

### Progress and Issue Report

- Our asynchronous and parallel workflow has been unsuccessful, and we believe
  that we need to collaborate together more
- Quests and NPCs are taking a long time to implement, so we may need to rescale
  our plans for levels 1-20 to just levels 1-15 or 1-10
- The build server we use to develop the map was completely vandalized by
  someone from France who connected to our server. Ultimately, we were able to
  recover most of our work but a significant amount of progress was lost
  nonetheless. We’re not sure how this bad actor discovered the server IP, but
  this served as an important lesson for us to tighten our security and create
  backups of the map more frequently.

### Plans for Next Week

- Continue working on gameplay levels 1-5
- Meet this weekend to work on quests collaboratively rather than asynchronously
- Prepare gameplay demo for beta release demos

### Meeting Agenda

- Discuss stat system
- Plan quests and non-player characters

## Contributions of Individual Team Members

### Goals from Last Week

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

### Progress and Issue Report

- Chien
  - Brainstormed possible overarching stories and complementary quests
    surrounding it
  - downloaded and learned how to use twine to keep track of quests
  - started trying to write some basic quests with npc placements
- Christopher
  - Added Fan of Knives skill.
  - Fixed stealth and adrenaline rogue skills.
  - Fixed github branch for skills (many merge conflicts)
- Connor
  - Created an unrefined player character creation, selection, and deletion GUI
  - Refactored player character persistence by using dependency injection to
    allow `Mmorpg`s to be provided with a `PersistenceService` interface
  - Refactored player character creation so that `Feature`s can set the result
    of player character creation
  - Cleaned up some junk lurking in the repository
- Eliot
  - Met with Connor and Chien and figured out how the actual quest
    giving/completion works
  - Scheduled a second meeting to do some story planning
  - Created a tutorial quest NPC which did not end up being included in the
    finished version
  - Issue 1: story is not yet structured enough to make 10 more NPCs, plan to
fix this at meeting Saturday
  - Issue 2: insufficient communication regarding the tutorial quest meant I
    didn’t get to use the NPC I created, will also fix this Saturday
- Kyle
  - Wrote a basic system for damage calculation. It still needs to be reviewed
    and fleshed out.
- Vincent
  - Recovering from illness.

### Plans for Next Week

- Chien
  - share overarching stories and complementary quests ideas to Saturday group
    meeting
  - work alone or with others to create as many quests as possible
- Christopher
  - Plan to double check all skill code for bugs.
  - Add a final skill to the warrior
  - Add skills that buff pre-existing skills
- Connor
  - Add automated building and testing workflow using GitHub actions
  - Work on quests and NPCs with content team to ensure we are making progress
  - Get consumable items working correctly
  - If time allows, improve character creation, selection, and deletion GUI
- Eliot
  - Do significant story planning at meeting Saturday
  - Schedule out specific NPCs to be created for the remainder of the quarter
  - Develop 15-25% of these within the week once I have a plan for which NPCs
    need to be developed in order to catch up with the Sprint schedule
  - Read behavior tree paper to prepare for meeting Saturday
- Kyle
  - Finish up the system for damage calculation.
  - Some stats currently have hard-coded values. I am going to start designing a
    system for managing and updating stats.
- Vincent
  - TBD
