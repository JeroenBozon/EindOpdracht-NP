package base;

import base.client.BlockClient;
import block.BlockDrag;
import javafx.application.Application;

public class ClientMain {

    public static void main(String[] args) {
        //BlockClient blockClient = new BlockClient("localhost", 10000);

//        new Thread(() -> {
//            Application.launch(BlockDrag.class, args);
//        }).start();
        Application.launch(BlockDrag.class, args);

        System.out.println("test");
        //blockClient.connect();
    }
}
