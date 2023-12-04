# User Manual

## Overview

The system allows a user to run a server instance of MCQuest, an MMORPG
Minecraft server with 3 fully functional classes (WIP), multiple zones (WIP),
many bosses (WIP), and dozens of quests (WIP)! If players wanted to run an
instance of MCQuest outside of officially sponsored servers in order to
privately host a free MMORPG that requires little installation, this project
will allow them to host and run the game on their own network.

## Installation

To install the Minecraft 1.19.2 client:

1. Install the [Minecraft launcher](https://www.minecraft.net/en-us/download).
2. Open the Minecraft launcher.
3. Navigate to the "Installations" tab within the launcher and click "New
   Installation".
4. Set the version to 1.19.2.
5. Click "Create".

To install the MCQuest server:

1. Ensure [Git](https://git-scm.com/downloads) is installed.
2. Run `git clone git@github.com:mc-quest/mc-quest.git`.

## Running the Software

From the root of the `mc-quest` repository, run `./gradlew run`. This will run
the server on `localhost`.

## Using the Software

To connect to the MCQuest server:

1. Launch the Minecraft 1.19.2 client, which was installed earlier.
2. Click the "Multiplayer" button to open the multiplayer menu.
3. Click "Add Server".
4. Enter "MCQuest" for the server name and `localhost` for the server address.
5. Set the "Server Resource Packs" option to "Enabled".
6. Click "Done" to add the server.
7. Join the MCQuest server that was just added.

After connecting to the server, you will need to create a character using the
provided menu. Upon logging in as your character, you will be provided with a
tutorial.

## Bug Reports

In the [issue tracker](https://github.com/mc-quest/mc-quest/issues/new/choose),
open a bug report using our provided template. The template requires you to:

1. Describe the bug.
2. Provide steps to reproduce the bug.
3. Explain the expected behavior.
4. Provide screenshots if applicable.
5. Provide any additional context, if applicable.

## Known Bugs

See our [issue tracker](https://github.com/mc-quest/mc-quest/issues/new/choose)
for known bugs.
