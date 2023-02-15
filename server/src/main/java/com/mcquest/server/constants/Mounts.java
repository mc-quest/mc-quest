package com.mcquest.server.constants;

import com.mcquest.server.mount.Mount;
import com.mcquest.server.mounts.TestMount;

public class Mounts {
    public static final Mount TEST_MOUNT = new TestMount();

    public static Mount[] all() {
        return new Mount[]{
                TEST_MOUNT
        };
    }
}
