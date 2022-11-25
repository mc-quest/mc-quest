package com.mcquest.server.mount;

public class Mount {
    private final int id;

    public Mount(int id) {
        this.id = id;
    }

    /**
     * TODO: Listen to ClientSteerVehiclePacket
     * packet.flags() == 1: jump
     * packet.flags() == 2: shift
     */

    public int getId() {
        return id;
    }
}
