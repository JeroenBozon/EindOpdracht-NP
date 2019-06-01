package base.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
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

    @Override
    public void run() {
        JSONParser parser = new JSONParser();

        new Thread(() -> {
            while (this.running) {
                try {
                    String input = "";
                    input = this.in.readUTF();
                    while (input.contains("<")) {
                        input += this.in.readUTF();
                        if (input.contains(">")) {
                            Scanner scanner = new Scanner(input);
                            scanner.useDelimiter("<");
                            scanner.next();
                            scanner.useDelimiter(">");
                            input = scanner.next();
                            input = input.substring(1);

                            this.server.receiveJson((JSONObject) parser.parse(input));
                            break;
                        }
                    }
                } catch (SocketException e) {
                    System.out.println("Disconnecting client...");
                    this.running = false;
                } catch (ParseException e) {
                    System.out.println("PARSE EXCEPTION");
                } catch (IOException e) {
                    System.out.println("IO EXCEPTION");
                }
            }
        }).start();

        try {
            this.in = new DataInputStream(this.socket.getInputStream());
            this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
            this.writer = new OutputStreamWriter(this.out, StandardCharsets.UTF_8);

            while (running) {
                this.sendJson(this.server.getBlockData());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //sends utf
    public void sendJson(JSONObject jsonObject) {
        try {
            this.writer.write("<" + jsonObject.toJSONString() + ">");
            this.writer.flush();

        } catch (Exception e) {
            this.server.removeClient(this);
            this.running = false;
            System.out.println("Client disconnected from " + this.socket.getInetAddress().getHostAddress() + ".");
        }
    }

}
