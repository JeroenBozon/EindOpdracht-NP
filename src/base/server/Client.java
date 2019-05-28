package base.server;

import base.server.BlockServer;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client implements Runnable {
    private Socket socket;
    private BlockServer server;
    private DataOutputStream out;
    private DataInputStream in;
    private OutputStreamWriter writer;
    private ObjectOutputStream objectOutputStream;

    public Client(Socket socket, BlockServer blockServer) {
        this.socket = socket;
        this.server = blockServer;
    }

    public void sendBlockData() {

    }

    @Override
    public void run() {

        try {
            this.in = new DataInputStream(this.socket.getInputStream());
            this.out = new DataOutputStream(this.socket.getOutputStream());
            this.writer = new OutputStreamWriter(this.out, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.in));

            while (true) {
                //writer.write(this.blockData.toJSONString());
                this.sendJson(this.server.getBlockData());
                //Thread.sleep(200);
                //writer.write("yeet\n");
                //writer.flush();

//                String line = null;
//                while ((line = reader.readLine()) != null) {
//
//                }

            }

        } catch (IOException e) {
            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
        }


//        try {
//            this.in  = new DataInputStream( this.socket.getInputStream() );
//            this.out = new DataOutputStream( this.socket.getOutputStream() );
//
//            //out.writeUTF("Avans ChatServer 1.2.3.4");
//
//            //this.name = in.readUTF();
//            //System.out.println("#### " + this.name + " joined the chat!");
////            this.server.sendToAllClients("#### " + this.name + " joined the chat!");
//
////            String message = "";
////            while ( !message.equals("stop") ) {
////                message = in.readUTF();
////                out.writeUTF(message);
////                System.out.println("Client send: " + message);
////                this.server.sendToAllClients("(" + this.name + "): " + message);
//
//            //}
//
//            this.socket.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



    }

    //sends utf
    public void sendJson(JSONObject jsonObject) {
        //todo send json to server
        try {
            this.writer.write("\n" + jsonObject.toJSONString() + "\n");
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
