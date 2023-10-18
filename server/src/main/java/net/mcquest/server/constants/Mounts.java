package net.mcquest.server.constants;

import net.mcquest.core.mount.Mount;
import net.mcquest.core.mount.MountType;

public class Mounts {
    public static final Mount TEST_MOUNT = new Mount("test_mount", MountType.GROUND);

    public static Mount[] all() {
        return new Mount[]{
                TEST_MOUNT
        };
    }
}
