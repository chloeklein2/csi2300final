import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import javafx.scene.paint.Color;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class project extends Application {
    
    private double iX, iY, eX, eY;
    private GraphicsContext gc;
    
    ColorPicker colorPicker = new ColorPicker();
    TextField textField = new TextField("1");
    TextField width= new TextField("800");    
    TextField height = new TextField("600");
    int size;
    
    int cWidth=800;
    int cHeight=600;
    Stack<CanvasState> undoHistory = new Stack<>();
    Canvas canvas = new Canvas(cWidth, cHeight);
    private enum tool{
        Draw, Rectangle, Erase, Ellipse
    }
    private tool activetool = tool.Draw;
    
    
    @Override
    public void start(Stage primaryStage) {
            
        GridPane gPane = new GridPane();
        BorderPane bPane = new BorderPane();
        
        width.setEditable(false);
        height.setEditable(false);
        gc = canvas.getGraphicsContext2D();
        gPane.setGridLinesVisible(false);
        
        //buttons
        Button savebtn= new Button("save");
        Button clearbtn = new Button("clear");
        Button confirmbtn = new Button("confirm✓");
        Button undobtn = new Button("undo");
        Button openbtn = new Button("open");
        Button erasebtn = new Button("erase");
        Button rectbtn = new Button("◻");
        Button ellipsebtn = new Button("○");
        Button drawbtn = new Button("brush");
        

        gPane.add(savebtn,0 , 0);
        gPane.add(clearbtn, 14, 2);
        gPane.add(colorPicker, 2, 0);
        gPane.add(new Label("brush size"), 0,2);
        gPane.add(confirmbtn, 2, 2);
        gPane.add(textField,1,2);
        gPane.add(erasebtn, 11, 2);
        gPane.add(rectbtn, 10, 2);
        gPane.add(ellipsebtn, 10, 0);
        gPane.add(drawbtn, 11, 0);
        gPane.add(new Label("canvas width"), 120,0);
        gPane.add(width, 130,0);
        gPane.add(undobtn,14,0);
        gPane.add(new Label("canvas height"), 120,2);
        gPane.add(height, 130,2);
        gPane.add(openbtn,1,0);
        
        textField.setPrefWidth(30);
        colorPicker.setPrefWidth(70);
        savebtn.setPrefWidth(55);
        colorPicker.setValue(Color.BLACK);
        
        
        clearbtn.setOnAction(e->{
            gc.setFill(Color.WHITE);
            gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        });
        undobtn.setOnAction(e->{
            undo();
        });
                
        drawbtn.setOnAction(e->
           activetool=tool.Draw
        );
        erasebtn.setOnAction(e->
            activetool=tool.Erase
                   
        );
        rectbtn.setOnAction(e->
           activetool= tool.Rectangle
            
        );
        ellipsebtn.setOnAction(e->
            activetool=tool.Ellipse
            
        );
        canvas.setOnMousePressed(e->{
            
                iX = e.getX();
                iY = e.getY();
            
            
        });
        canvas.setOnMouseDragged(e->{
            if (activetool == tool.Draw) {
                eX = e.getX();
                eY = e.getY();
                draw();
                iX = eX;
                iY = eY;
                
            }
            else if(activetool==tool.Erase){
                eX = e.getX();
                eY = e.getY();
                erase();
                iX = eX;
                iY = eY;
            }
        });
        canvas.setOnMouseReleased(e->{
            if (activetool == tool.Rectangle) {
                eX = e.getX();
                eY = e.getY();
                drawRectangle();
                
            }
            else if (activetool == tool.Ellipse){
                eX = e.getX();
                eY = e.getY();
                drawEllipse();
            }

        });
        savebtn.setOnAction(e->{
            FileChooser save = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG file", "*.PNG");
            save.getExtensionFilters().add(extFilter);
            save.setTitle("Save");
    
            File file = save.showSaveDialog(primaryStage);
            
            if (file != null) {
                try {
                    WritableImage writableImage = new WritableImage(cWidth, cHeight);
                    canvas.snapshot(null, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {                    
                    System.out.println("error");
                }
            }
        });
        openbtn.setOnAction(e->{
            
            FileChooser open = new FileChooser();
            open.setTitle("Open");
            File file = open.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    InputStream io = new FileInputStream(file);
                    Image img = new Image(io);
                    gc.drawImage(img, 0, 0);
                } catch (IOException ex) {
                    System.out.println("error");
                }
            }
            
        }); 
        confirmbtn.setOnAction((e)->{
             size = Integer.parseInt(textField.getText());
             
        });
        
        
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        bPane.setCenter(canvas);
        
        bPane.setTop(gPane);
        primaryStage.setScene(new Scene(bPane, canvas.getWidth()+100, canvas.getHeight()+100,Color.LIGHTGREY));
        primaryStage.setTitle("EvilDraw");
        primaryStage.show();
    }
    
    private void draw(){               
        gc.setStroke(colorPicker.getValue());
        gc.setLineWidth(size);
        gc.strokeLine(iX, iY, eX, eY);
        saveState();
        
    }
    private void erase(){               
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(size);
        gc.strokeLine(iX, iY, eX, eY);
        saveState();
        
    }
    
    private void drawRectangle() {
        double width = Math.abs(eX - iX);
        double height = Math.abs(eY - iY);
        double x = Math.min(iX, eX);
        double y = Math.min(iY, eY);
        gc.setFill(colorPicker.getValue());
        gc.fillRect(x, y, width, height);
        saveState();
    }
    private void drawEllipse(){
        double width = Math.abs(eX - iX);
        double height = Math.abs(eY - iY);
        double x = Math.min(iX, eX);
        double y = Math.min(iY, eY);
        gc.setFill(colorPicker.getValue());
        gc.fillOval(x, y, width, height);
        saveState();
    }
    
    //undo
    private void saveState() {
        CanvasState currentState = new CanvasState(
                canvas.snapshot(null, null)
        );        
        undoHistory.push(currentState);
    }
    private static class CanvasState {
        private Image image;

        public CanvasState(Image image) {
            this.image = image;
        }

        public Image getImage() {
            return image;
        }
    }
    private void undo() {
        if (!undoHistory.isEmpty()) {            
            CanvasState lastState = undoHistory.pop();
            gc.drawImage(lastState.getImage(), 0, 0);
        }
    }
   
    public static void main(String[] args) {
        launch(args);
    }


}
