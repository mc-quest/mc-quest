package com.mcquest.server.constants;

import com.mcquest.server.mount.Mount;
import com.mcquest.server.mount.MountType;

public class Mounts {
    public static final Mount TEST_MOUNT = new Mount(1, MountType.GROUND);

    public static Mount[] all() {
        return new Mount[]{
                TEST_MOUNT
        };
    }
}
