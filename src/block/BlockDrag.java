package block;

import base.client.BlockClient;
import base.server.Client;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class BlockDrag extends Application {
    private ArrayList<Block> blocks;
    private int cubeSize;
    private ResizableCanvas canvasAttribute;
    private boolean firstCheck;
    private double oldMouseX;
    private double oldMouseY;
    private Block selectedBlock;
    private double xFromSelected;
    private double yFromSelected;
    private BlockClient blockClient;
    private FXGraphics2D graphics;
    private Button sendToServerButton;
    private Button receiveFromServerButton;

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane mainPane = new BorderPane();
        ResizableCanvas canvas = new ResizableCanvas(g -> draw(g), mainPane);
        mainPane.setCenter(canvas);

        VBox vBox = new VBox();
        HBox hBox = new HBox();

        sendToServerButton = new Button();
        receiveFromServerButton = new Button();

        sendToServerButton.setText("Send positions to server!");
        receiveFromServerButton.setText("Receive positions from server!");

        sendToServerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                blockClient.sendBlockData(writeJson());
            }
        });

        receiveFromServerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateBlocks(blockClient.getBlockData());
            }
        });

        hBox.getChildren().addAll(sendToServerButton, receiveFromServerButton);

        cubeSize = 50;
        Random random = new Random();
        blocks = new ArrayList<>();
        blocks.add(new Block(random.nextInt((int)canvas.getWidth() - cubeSize), random.nextInt((int)canvas.getHeight() - cubeSize), Color.BLUE));
        blocks.add(new Block(random.nextInt((int)canvas.getWidth() - cubeSize), random.nextInt((int)canvas.getHeight() - cubeSize), Color.CYAN));
        blocks.add(new Block(random.nextInt((int)canvas.getWidth() - cubeSize), random.nextInt((int)canvas.getHeight() - cubeSize), Color.DARK_GRAY));
        blocks.add(new Block(random.nextInt((int)canvas.getWidth() - cubeSize), random.nextInt((int)canvas.getHeight() - cubeSize), Color.GRAY));
        blocks.add(new Block(random.nextInt((int)canvas.getWidth() - cubeSize), random.nextInt((int)canvas.getHeight() - cubeSize), Color.GREEN));
        blocks.add(new Block(random.nextInt((int)canvas.getWidth() - cubeSize), random.nextInt((int)canvas.getHeight() - cubeSize), Color.LIGHT_GRAY));
        blocks.add(new Block(random.nextInt((int)canvas.getWidth() - cubeSize), random.nextInt((int)canvas.getHeight() - cubeSize), Color.MAGENTA));
        blocks.add(new Block(random.nextInt((int)canvas.getWidth() - cubeSize), random.nextInt((int)canvas.getHeight() - cubeSize), Color.ORANGE));
        blocks.add(new Block(random.nextInt((int)canvas.getWidth() - cubeSize), random.nextInt((int)canvas.getHeight() - cubeSize), Color.PINK));
        blocks.add(new Block(random.nextInt((int)canvas.getWidth() - cubeSize), random.nextInt((int)canvas.getHeight() - cubeSize), Color.RED));
        blocks.add(new Block(random.nextInt((int)canvas.getWidth() - cubeSize), random.nextInt((int)canvas.getHeight() - cubeSize), Color.YELLOW));
        canvasAttribute = canvas;

        for (int i = 0; i < this.blocks.size(); i++) {
            this.blocks.get(i).setBlockId(i);
        }

        this.graphics = new FXGraphics2D(this.canvasAttribute.getGraphicsContext2D());

        vBox.getChildren().addAll(mainPane, hBox);

        primaryStage.setScene(new Scene(vBox));
        primaryStage.setTitle("Block Dragging");
        primaryStage.show();
        
        canvas.setOnMousePressed(e -> mousePressed(e));
        canvas.setOnMouseReleased(e -> mouseReleased(e));
        canvas.setOnMouseDragged(e -> mouseDragged(e));

        this.setupClient();
        this.draw(this.graphics);
    }

    private void setupClient() {
        boolean dataReceived = false;
        this.blockClient = new BlockClient("localhost", 10000);
        this.blockClient.connect();

        while (!dataReceived) {
            try {
                this.updateBlocks(this.blockClient.getBlockData());
                dataReceived = true;
            } catch (NullPointerException e) {
                System.out.println("Block data is not available yet, retrying in 5 seconds...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    
    
    public void draw(FXGraphics2D graphics) {
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, (int)canvasAttribute.getWidth(), (int)canvasAttribute.getHeight());

        for (Block block : blocks) {
            graphics.setColor(block.getColor());
            graphics.fillRect(block.getX(), block.getY(), cubeSize, cubeSize);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(block.getX(), block.getY(), cubeSize, cubeSize);
        }

    }

    @Override
    public void stop() throws Exception {
        this.blockClient.stop();
        super.stop();
    }

    private void mousePressed(MouseEvent e) {
        for (Block block : blocks) {
            double xFromCorner = e.getX() - block.getX();
            double yFromCorner = e.getY() - block.getY();
            if (xFromCorner >= 0 && xFromCorner <= cubeSize && yFromCorner >= 0 && yFromCorner <= cubeSize) {
                this.selectedBlock = block;
                this.xFromSelected = xFromCorner;
                this.yFromSelected = yFromCorner;
                firstCheck = true;
            }
        }

    }

    private void mouseReleased(MouseEvent e) {
        firstCheck = false;
    }

    private void mouseDragged(MouseEvent e) {
        if (firstCheck) {
            selectedBlock.setX((int)(e.getX() - xFromSelected));
            selectedBlock.setY((int)(e.getY() - yFromSelected));
        }
        draw(this.graphics);
    }

    private JSONObject writeJson() {
        JSONArray blockArrayInfo = new JSONArray();

        for (Block block : this.blocks) {
            JSONObject blockInfo = new JSONObject();
            blockInfo.put("blockID", block.getBlockId());
            blockInfo.put("blockX", block.getX());
            blockInfo.put("blockY", block.getY());
            blockArrayInfo.add(blockInfo);
            System.out.println("Block updated");
        }
        JSONObject blockData = new JSONObject();
        blockData.put("blockdata", blockArrayInfo);

        System.out.println("Data updated");
        return blockData;
    }

    private void saveJson(JSONObject blockData) {
        try {
            File saveFile = new File("data.json");
            PrintWriter file = new PrintWriter(new FileWriter(saveFile));
            file.write(blockData.toJSONString());
            System.out.println("printed");
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateBlocks(JSONObject blockData) {
        JSONArray jsonArray = (JSONArray) blockData.get("blockdata");

        for (Block block : this.blocks) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                //System.out.println(Math.toIntExact( (long) jsonObject.get("blockID")) );
                if ( this.getJsonInt(jsonObject.get("blockID")) == block.getBlockId() ) {
                    block.setX(this.getJsonInt(jsonObject.get("blockX")));
                    block.setY(this.getJsonInt(jsonObject.get("blockY")));
                }
            }
        }

        this.draw(this.graphics);
    }

    //todo liever niet dit
    private int getJsonInt(Object object) {
        try {
            return Math.toIntExact( (long) object);
        } catch (Exception e) {
            System.out.println("Could not convert to int!");
        }
        return -1;
    }

}
