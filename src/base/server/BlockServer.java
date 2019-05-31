package base.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlockServer {
    private ServerSocket server;
    private int port;
    private Thread serverUserSearchThread;
    private Thread serverDataThread;
    private List<Client> clients;
    private List<Thread> threads;
    private JSONObject blockData;
    private DataInputStream in;
    private Socket socket;

    public BlockServer(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        this.threads = new ArrayList<>();
        this.readJson();
    }

    private void readJson() {
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader("data.json")) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            this.blockData = jsonObject;
            System.out.println("Read JSON");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean start() {
        try {
            this.server = new ServerSocket(port);

            new Thread(() -> {
                while (true) {
                    System.out.println("Waiting for clients to connect.");
                    try {
                        socket = this.server.accept();
                        System.out.println("Client connected from " + socket.getInetAddress().getHostAddress() + ".");

                        Client client = new Client(socket, this);
                        Thread threadClient = new Thread(client);
                        threadClient.start();

                        this.clients.add(client);
                        this.threads.add(threadClient);

                        System.out.println("Total clients connected: " + this.clients.size());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(100);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Thread.yield();
                }

            }).start();

            JSONParser parser = new JSONParser();
            System.out.println("before new thread");

            new Thread(() -> {
                System.out.println("Update blockdata started");

                try {
                    this.socket = this.server.accept();
                    System.out.println("onder socket");

                    this.in = new DataInputStream(socket.getInputStream());

                    while(true) {

                        String input = "";
                        input = this.in.readUTF();

                        System.out.println("In first while loop: " + input);

                        while (input.contains("<")) {
                            input += this.in.readUTF();
                            System.out.println("In second while loop: " + input);
                            if (input.contains(">")) {
                                Scanner scanner = new Scanner(input);
                                scanner.useDelimiter("<");
                                scanner.next();
                                scanner.useDelimiter(">");
                                input = scanner.next();
                                input = input.substring(1);

                                //System.out.println(input);

                                this.blockData = (JSONObject) parser.parse(input);
                                System.out.println("Blockserver: blockdata updated");
                                //todo make blockserver actually update the blockdata
                                break;
                            }
                        }
                    }

                } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("ioexception");
                } catch (ParseException e) {
                    //e.printStackTrace();
                    System.out.println("parseexception");
                }
            }).start();

            System.out.println("Server is started and listening on port " + this.port);

        } catch (IOException e) {
            System.out.println("Could not connect: " + e.getMessage());
            return false;
        }

        return true;
    }

    public void removeClient(Client client) {
        this.clients.remove(client);
    }

    public void recieveJson(JSONObject jsonObject) {
        this.blockData = jsonObject;
        System.out.println("Received JSON");
    }

    public JSONObject getBlockData() {
        return blockData;
    }
}
