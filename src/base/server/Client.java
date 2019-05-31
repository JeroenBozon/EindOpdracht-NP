package base.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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
        JSONParser parser = new JSONParser();

        new Thread(() -> {
            while (this.running) {
                try {
                    String input = "";
                    input = this.in.readUTF();
                    System.out.println("Client trying thread");
                    while (input.contains("<")) {
                        input += this.in.readUTF();
                        if (input.contains(">")) {
                            Scanner scanner = new Scanner(input);
                            scanner.useDelimiter("<");
                            scanner.next();
                            scanner.useDelimiter(">");
                            input = scanner.next();
                            input = input.substring(1);

                            //System.out.println(input);
                            System.out.println(input);
                            this.server.receiveJson((JSONObject) parser.parse(input));
                            System.out.println("Client: blockdata updated");
                            break;
                        }
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


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
        this.server.receiveJson(jsonObject);
    }

}
