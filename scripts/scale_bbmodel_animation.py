#!/usr/bin/env python

"""Adjusts Blockbench model animation speed."""

import sys
import os.path
import json


def error(message: str):
    print(message)
    sys.exit(1)


def main():
    if len(sys.argv) != 4:
        error("Usage: %s file_name animation_name scale" % sys.argv[0])

    file_name = sys.argv[1]
    if not os.path.isfile(file_name):
        error("File %s does not exist" % file_name)
    if not file_name.endswith(".bbmodel"):
        error("File %s is not a Blockbench model" % file_name)
    with open(file_name, "r") as file:
        model = json.load(file)

    animation_name = sys.argv[2]
    animations = [animation for animation in model["animations"]
                  if animation["name"] == animation_name]
    if len(animations) == 0:
        error("Animation %s does not exist" % animation_name)
    animation = animations[0]

    try:
        scale = float(sys.argv[3])
    except ValueError:
        error("Scale %s is not a positive number" % sys.argv[3])
    if scale <= 0:
        error("Scale %f is not a positive number" % scale)

    animation["length"] /= scale
    if "animators" in animation:
        for animator in animation["animators"].values():
            for keyframe in animator["keyframes"]:
                keyframe["time"] /= scale

    with open(file_name, "w") as file:
        json.dump(model, file)


if __name__ == "__main__":
    main()
