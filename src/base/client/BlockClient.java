package base.client;

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

    public BlockClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect(){
        try {

            this.socket = new Socket(this.host, this.port);

            this.in = new DataInputStream( this.socket.getInputStream() );
            this.out = new DataOutputStream( this.socket.getOutputStream() );

//            Scanner scanner = new Scanner( System.in );
//
//            String server = dataInputStream.readUTF();
//            System.out.println(server);

            //HIERONDER WORDT EEN NAAM GEGEVEN AAN DE this.name ZODAT IEMAND EEN NAAM HEEFT

//            System.out.print("What is your name: ");
//            this.name = scanner.nextLine();
//            dataOutputStream.writeUTF(this.name);

            JSONParser parser = new JSONParser();
            new Thread ( () -> {
                while ( true ) {
                    try {
                        //reads utf
                        //todo read utf
                        //this.blockData = (JSONObject) parser.parse(this.in.readUTF());

                        String input = this.in.readUTF();
                        Scanner scanner = new Scanner(input);
                        this.blockData = (JSONObject) parser.parse(scanner.next());
                        //todo detect and handle failed json strings

//                        while (input.contains("start")) {
//                            input += this.in.readUTF();
//                            if (input.contains("stop")) {
//                                input = input.substring(input.indexOf("start"), input.indexOf("stop"));
//                                System.out.println(input + "\n");
//                                break;
//                            }
//                        }
//                        input = input.substring(input.indexOf("start"), input.indexOf("stop"));
//                        System.out.println(input + "\n");

//                    } catch (EOFException e) {
//                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
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

    private void sendBlockData(JSONObject jsonObject) {
        try {
            this.out.writeUTF(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getBlockData() {
        return blockData;
    }
}
