package base.server;

import base.server.BlockServer;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Client implements Runnable {
    private Socket socket;
    private BlockServer server;
    private DataOutputStream out;
    private DataInputStream in;
    private OutputStreamWriter writer;
    private ObjectOutputStream objectOutputStream;
    private boolean running;

    public Client(Socket socket, BlockServer blockServer) {
        this.socket = socket;
        this.server = blockServer;
        this.running = true;
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

            while (running) {
                this.sendJson(this.server.getBlockData());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //sends utf
    public void sendJson(JSONObject jsonObject) {
        //todo send json to server
        try {
            this.writer.write("<" + jsonObject.toJSONString() + ">");
            this.writer.flush();
        //} catch (IOException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
            this.server.removeClient(this);
            this.running = false;
            System.out.println("Client disconnected from " + this.socket.getInetAddress().getHostAddress() + ".");
        }
    }

    public void recieveJson(JSONObject jsonObject) {
        this.server.recieveJson(jsonObject);
    }

}
