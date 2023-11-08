# MCQuest (`v0.1.0`)

The implementation of MCQuest, an MMORPG Minecraft server. Players embark on
quests, explore an open world, delve through dungeons, level their character,
acquire items and gear, and slay powerful foes with others in a medieval fantasy
setting.

## Project Structure

MCQuest is divided into the following subprojects:

- `core`: The core APIs for building and managing MMORPGs. Creates high level
  abstractions for `server` to create MMORPG content.
- `server`: Uses the `core` API to implement and serve an MMORPG.

## Running the Server

1. Execute `gradlew run` to start the server.
2. Connect to `localhost` through the Minecraft client to connect.

## Building Fat Jar

Execute `gradlew build` to build the Fat Jar.

## Running Tests

Execute `gradlew test` to run tests.

## Operational Use Cases

- Playing with friends
- Selecting a class to play as
- Fighting enemies
- Completing quests
- Using skills
- Using the map
- Exploring the world
