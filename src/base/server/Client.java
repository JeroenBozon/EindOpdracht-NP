package base.server;

import base.server.BlockServer;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {
    private Socket socket;
    private BlockServer server;
    private DataOutputStream out;
    private DataInputStream in;
    private String name;

    public Client(Socket socket, BlockServer blockServer) {
        this.socket = socket;
        this.server = blockServer;
    }

    public void sendBlockData() {

    }

    @Override
    public void run() {

        try {
            this.in  = new DataInputStream( this.socket.getInputStream() );
            this.out = new DataOutputStream( this.socket.getOutputStream() );

            out.writeUTF("Avans ChatServer 1.2.3.4");

            this.name = in.readUTF();
            System.out.println("#### " + this.name + " joined the chat!");
//            this.server.sendToAllClients("#### " + this.name + " joined the chat!");

            String message = "";
            while ( !message.equals("stop") ) {
                message = in.readUTF();
                out.writeUTF(message);
                System.out.println("Client send: " + message);
//                this.server.sendToAllClients("(" + this.name + "): " + message);

            }

            this.socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void sendJson(JSONObject jsonObject) {
        
    }

}
