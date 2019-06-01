package base.client;

import block.BlockDrag;
import javafx.application.Application;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class BlockClient {

    private Socket socket;
    private String host;
    private int port;
    private DataInputStream in;
    private DataOutputStream out;
    private OutputStreamWriter writer;
    private JSONObject blockData;
    private JSONObject sendData;
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
            this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
            this.writer = new OutputStreamWriter(this.out, StandardCharsets.UTF_8);
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
                    } catch (Exception e) {
                        System.out.println("Could not connect.");
                        this.running = false;
                    }
                }

            }).start();


            this.sendBlockData(this.blockData);

            new Thread(() -> {
                while(running) {
                    try {
                        this.writer.write("<" + this.sendData.toJSONString() + ">");
                        this.writer.flush();

                        //System.out.println(this.sendData.toJSONString());

                        //Thread.sleep(200);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e){
                        System.out.println("Data not available yet, retrying in a bit...");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        //} catch (InterruptedException e) {
                        //e.printStackTrace();
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

    public void sendBlockData(JSONObject jsonObject) {
        System.out.println("try sendBlockData");
        this.sendData = jsonObject;
    }

    public JSONObject getBlockData() throws NullPointerException {
        return blockData;
    }
}
