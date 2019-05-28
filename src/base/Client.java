package base;

import base.client.BlockClient;

public class Client {

    public static void main(String[] args) {
        BlockClient blockClient = new BlockClient("localhost", 10000);

        blockClient.connect();
    }
}
