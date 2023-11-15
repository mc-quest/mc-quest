# Developer Manual

## Obtaining the Source Code

Run `git clone git@github.com:mc-quest/mc-quest.git`. All of our code is
contained in this repository.

## Project Layout

The project contains the following directories:

- `core`: Java project implementing the core engine of MCQuest. Includes the
  physics engine, object manager, instance manager, and much more. The source
  files are found within `core/src/main/java/net/mcquest/core`. Resources, such
  as textures and data files, are found within `core/src/main/resources`. Tests
  are found within `core/src/test/java/net/mcquest/core`.
- `server`: Uses `core` to implement and serve an MMORPG with custom content.
  The source files are contained in `server/src/main/java/net/mcquest/server`.
  Non-player characters are implemented in the `npc` package. Features, the
  modular units of interactivity that add content to the MMORPG are implemented
  in the `features` package. The `constants` package contains bindings for all
  the constant data used in the MMORPG, such as quests, instances, music, and
  models. `server/src/main/resources` contains all resources for core including
  textures, audio, models, JSON data files.
- `.github`: Contains issue templates and CI workflows.
- `scripts`: Contains simple scripts to help with development, including a
  script to adjust the animation speeds of our 3D models.

## Building the Software

Run `gradlew build` to create a fat JAR at `server/build/libs/server-all.jar`.
Alternatively, one can simply run `gradlew run` to run the server without
building a JAR.

## Testing the Software

Run `gradlew test`.

## Adding Tests

We use [JUnit](https://junit.org/junit5/docs/current/user-guide/) for testing.
To add unit tests to `core`, add a file under `core/src/java/net/mcquest/core`.
To add unit tests to `server`, add a file under
`server/src/java/net/mcquest/server`. However, we currently do not have any unit
tests for `server`, as we prefer to playtest it (`core` does all the heavy
lifting, after all).

## Releases

MCQuest uses [semantic versioning](https://semver.org/) to name releases. To
create a release, tag the release commit with the release version. The version
number must be updated within the README. One can attach a fat JAR created by
`gradlew build` to the release.
