package base;

import base.server.BlockServer;

public class ServerMain {

    public static void main(String[] args) {

        int port = 8936;
        BlockServer blockServer = new BlockServer(port);
        blockServer.start();

        while (true) {
            try {
                Thread.sleep(10000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Thread.yield();
        }

    }
}
