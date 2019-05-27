package base;

import base.server.BlockServer;

public class Main {

    public static void main(String[] args) {

        int port = 10000;
        BlockServer blockServer = new BlockServer(port);
        blockServer.start();

        while (true) {

        }

    }
}
