package base.server;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BlockServer {
    private ServerSocket server;
    private int port;
    private Thread serverThread;
    private List<Client> clients;
    private List<Thread> threads;


    public BlockServer(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        this.threads = new ArrayList<>();
    }

    public boolean start() {
        try {
            this.server = new ServerSocket(port);

            this.serverThread = new Thread(() -> {
                while (true) {
                    System.out.println("Waiting for clients to connect.");
                    try {
                        Socket socket = this.server.accept();
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

            });

            this.serverThread.start();
            System.out.println("Server is started and listening on port " + this.port);

        } catch (IOException e) {
            System.out.println("Could not connect: " + e.getMessage());
            return false;
        }

        return true;
    }

    public void recieveJson(JSONObject jsonObject) {

    }

}
