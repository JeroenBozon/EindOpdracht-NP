package block;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
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

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane mainPane = new BorderPane();
        ResizableCanvas canvas = new ResizableCanvas(g -> draw(g), mainPane);
        mainPane.setCenter(canvas);

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

        draw(new FXGraphics2D(canvas.getGraphicsContext2D()));
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle("Block Dragging");
        primaryStage.show();
        
        canvas.setOnMousePressed(e -> mousePressed(e));
        canvas.setOnMouseReleased(e -> mouseReleased(e));
        canvas.setOnMouseDragged(e -> mouseDragged(e));


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
    
    
    
    public static void main(String[] args) {
        launch(BlockDrag.class);
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
        draw(new FXGraphics2D(canvasAttribute.getGraphicsContext2D()));

    }

    public void writeJson() {
        JSONArray blockArrayInfo = new JSONArray();

        for (Block block : this.blocks) {
            JSONObject blockInfo = new JSONObject();
            blockInfo.put("blockID", block.getBlockId());
            blockInfo.put("blockX", block.getX());
            blockInfo.put("blocky", block.getY());
            blockArrayInfo.add(blockInfo);
        }
        JSONObject blockData = new JSONObject();

        try {
            PrintWriter file = new PrintWriter(new FileWriter("data.json"));
            file.write(blockData.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
