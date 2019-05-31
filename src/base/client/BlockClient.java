package base.client;

import block.BlockDrag;
import javafx.application.Application;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class BlockClient {

    private Socket socket;
    private String host;
    private int port;
    private DataInputStream in;
    private DataOutputStream out;
    private JSONObject blockData;
    private boolean running;

    public BlockClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.running = true;
    }

    public boolean connect() {
        try {
            this.socket = new Socket(this.host, this.port);
            this.in = new DataInputStream(this.socket.getInputStream());
            this.out = new DataOutputStream(this.socket.getOutputStream());
            JSONParser parser = new JSONParser();

            /**
             * This thread parses the complete json text
             */
            new Thread(() -> {
                while (this.running) {
                    try {
                        String input = this.in.readUTF();
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
                                this.blockData = (JSONObject) parser.parse(input);
                                //todo properly pass this object to blockdrag
                                break;
                            }
                        }

//                        if (!launched) {
//                            new Thread(() -> {
//                                Application.launch(BlockDrag.class);
//                            }).start(); //todo sync blocks
//                            launched = true;
//                        }

                    } catch (IOException e) {
                        //e.printStackTrace();
                        System.out.println("ioexception");
                    } catch (ParseException e) {
                        //e.printStackTrace();
                        System.out.println("parseexception");
                    }
                }
            }).start();

            //this.socket.close();

        } catch (EOFException e) {
            System.out.println("Could not connect with the server on " + this.host + " with port " + this.port + ": " + e.getMessage());
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void stop() {
        this.running = false;
    }

    private void sendBlockData(JSONObject jsonObject) {
        try {
            this.out.writeUTF(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getBlockData() throws NullPointerException {
        return blockData;
    }
}
