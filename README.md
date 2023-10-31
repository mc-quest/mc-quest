# MCQuest

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
