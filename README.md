# MCQuest `v0.1.1`

The implementation of MCQuest, an MMORPG Minecraft server. Players embark on
quests, explore an open world, delve through dungeons, level their character,
acquire items and gear, and slay powerful foes with others in a medieval fantasy
setting.

## Project Structure (For more Information see Developer Manual)

MCQuest is divided into the following subprojects:

- `core`: The core APIs for building and managing MMORPGs. Creates high-level
  abstractions for `server` to create MMORPG content.
- `server`: Uses the `core` API to implement and serve an MMORPG.

## Running the Server (For more Information see User and Developer Manuals)

1. Execute `./gradlew run` to start the server.
2. Connect to `localhost` through the Minecraft client to connect.

## Building Fat JAR (For more information see Developer Manual)

Execute `./gradlew build` to build the fat JAR.

## Running Tests (For more information see Developer Manual)

Execute `./gradlew test` to run tests.

## Operational Use Cases (Completed Features)

- Completing a fully functional tutorial
- Playing with friends
- Playing one of three completely unique classes
- Fighting enemies
- Completing quests
- Reading one's quest journal
- Using skills
- Using the minimap
- Exploring a massive world
- Delving through multiple unique dungeons
- Acquiring a myriad of unique loot items
- Meeting custom NPCS with unique animations
- Hosting a private version of MCQuest
- Creating a new character and persistently saving that character's data
- Leveling up through completing quests and slaying monsters
