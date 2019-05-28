package base.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class BlockClient {

    private Socket socket;
    private String host;
    private int port;
    private String name;

    public BlockClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect(){
        try {
            this.socket = new Socket(this.host, this.port);

            DataInputStream dataInputStream = new DataInputStream( this.socket.getInputStream() );
            DataOutputStream dataOutputStream = new DataOutputStream( this.socket.getOutputStream() );

            Scanner scanner = new Scanner( System.in );

            String server = dataInputStream.readUTF();
            System.out.println(server);

            //HIERONDER WORDT EEN NAAM GEGEVEN AAN DE this.name ZODAT IEMAND EEN NAAM HEEFT

//            System.out.print("What is your name: ");
//            this.name = scanner.nextLine();
//            dataOutputStream.writeUTF(this.name);

            new Thread ( () -> {
                while ( true ) {
                    try {
                        System.out.println(dataInputStream.readUTF());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            //HIER ONER IS VOOR HET STOPPEN VAN DE CLIENT DUS ALS JE STOP INVOERT STOPT HET

//            String message = "";
//            while ( !message.equals("stop" ) ) {
//                System.out.print("> ");
//                message = scanner.nextLine();
//                dataOutputStream.writeUTF(message);
//
//                //System.dataOutputStream.println("Server response: " + dataInputStream.readUTF());
//            }

            this.socket.close();

        } catch (IOException e) {
            System.out.println("Could not connect with the server on " + this.host + " with port " + this.port + ": " + e.getMessage());
            return false;
        }

        return true;
    }
}
