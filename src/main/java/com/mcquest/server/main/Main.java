package com.mcquest.server.main;

import com.mcquest.server.api.MmorpgServer;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;

    public static void main(String[] args) {
        MmorpgServer server = new MmorpgServer(SERVER_ADDRESS, SERVER_PORT);
        server.start();
    }
}
