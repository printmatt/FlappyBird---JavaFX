import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

//import Event Handler
import javafx.event.EventHandler;
//mouse events
import javafx.scene.input.MouseEvent;
/*
   Author: Matt Vang
   Description: Flappy bird game remake
   Source for the image files:
   https://github.com/sourabhv/FlapPyBird/tree/master/assets/sprites
   
   PLEASE CLICK ON WAITING SCREEN TO START GAME
   */
public class FlappyBird extends Application
{
    public static void main(String[] args)
    {
        try
        {
            launch(args);
        }
        catch (Exception error)
        {
            error.printStackTrace();
        }
        finally
        {
            System.exit(0);
        }
    }
    
    // Use this method to approximate when bird passes the pipes +1 score
    public static boolean nearEqual(double birdValue, double pipeValue, double diffPercent)
    {
        double actualDiff = Math.abs(birdValue - pipeValue);         
        double acceptableDiff = diffPercent/100 * pipeValue;  
        return actualDiff < acceptableDiff;                                   
    }
    
    //initialize Score = 0
    double score =0;
    int elapsedTime=0;
    double speed=0;
    public void start(Stage mainStage)
    {
        mainStage.setTitle("Flappy Bird");

        BorderPane root = new BorderPane();
        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        Canvas canvas = new Canvas(400,600);
        GraphicsContext context = canvas.getGraphicsContext2D();
        root.setCenter(canvas);
        
        //Wait to start game
        Sprite wait = new Sprite();
        Image waitResized = new Image("flapimages/message.png",400,600,false,false);
        wait.setImage(waitResized);
        wait.position.set(200,300);
        wait.render(context);
        
        //Set background and resize to fit
        Sprite background = new Sprite();
        Image backgroundResized = new Image("flapimages/background-day.png",400,600,false,false);
        background.setImage(backgroundResized);
        background.position.set(200,300);
        Sprite base = new Sprite();
        Image baseResized = new Image("flapimages/base.png",400,112,false,false);
        base.setImage(baseResized);
        base.position.set(200,544);
       
        
        
        //Set bird 
        Sprite bird = new Sprite();
        bird.setImage("flapimages/redbird-upflap.png");
        bird.position.set(100,300);
        bird.velocity.setLength(1);
        bird.velocity.setAngle(90);
        
        //List of keys pressed
        ArrayList<String> keysPressedList = new ArrayList<String>();
        // To get bird jump
        ArrayList<String> keyJustPressedList = new ArrayList<String>();
        // List of pipes
        ArrayList<Sprite> pipesList = new ArrayList<Sprite>();
        //Store keys
        mainScene.setOnKeyPressed(
            (KeyEvent event)->
            {
                String name = event.getCode().toString();
                if(!keysPressedList.contains(name))
                {
                    keysPressedList.add(name);
                    keyJustPressedList.add(name);
                }
            }
        );
        
        //Remove keys
        mainScene.setOnKeyReleased(
            (KeyEvent event)->
            {
                String name = event.getCode().toString();
                if(keysPressedList.contains(name))
                {
                    keysPressedList.remove(name);
                }
            }
        );
        
        AnimationTimer gameLoop = new AnimationTimer()
        {
            public void handle(long nanotime)
            {
                //keep track of elapsed time to generate pipes
                elapsedTime += 1;
                // Show back ground and the bottom base
                background.render(context);
                base.render(context);
                
                 
                //bird movement, after jumping speed is very small
                if(keyJustPressedList.contains("SPACE")){
                    bird.position.add(0,-35);
                    bird.rotation = -30;
                    bird.velocity.setLength(1);
                    bird.velocity.setAngle(90);
                    speed=20.0;
                }
                keyJustPressedList.clear();
                
                // Set bird's falling movement that goes faster when falling
                if(bird.velocity.getLength() < 120)
                {
                    speed = 20.0+speed;
                    bird.velocity.setLength(speed);
                }
                else{
                    bird.velocity.setLength(120);
                }
                
                // Set rotation when falling
                if(bird.velocity.getLength()>50)
                    if(bird.rotation<60)
                    bird.rotation += 1;
                
                // Lock the bird inside window
                if(bird.position.y<=12)
                    bird.position.set(100,12);
                if (bird.position.y>=588)
                    bird.position.set(100,588);
                    
                
                bird.update(1/60.0);
                
                
                // Set up pipes so that they appear every 240 of elapsedTime
                if(elapsedTime % 120 == 0)
                {
                    //Set pipes
                    Sprite pipeTop = new Sprite();
                    Sprite pipeBot = new Sprite();
                    pipeTop.setImage("flapimages/pipe-top.png");
                    pipeBot.setImage("flapimages/pipe-bot.png");
                    
                    //Spaces appearing in random but uniformed
                    double i = 150.0*Math.random();
                    pipeTop.position.set(400,i);
                    pipeBot.position.set(400,i+160+160+120);
                    
                    //Pipes are moving toward the bird
                    pipeTop.velocity.setLength(120);
                    pipeTop.velocity.setAngle(180);
                    pipeBot.velocity.setLength(120);
                    pipeBot.velocity.setAngle(180);
                    
                    pipesList.add(pipeBot);
                    pipesList.add(pipeTop);
                }
                
                // Update pipes location
                for (Sprite pipe: pipesList)
                pipe.update(1/60.0);
                
                // Show Bird
                bird.render(context);
                
                //Show Pipes
                for (Sprite pipe: pipesList)
                pipe.render(context);
                
                // Check lose condition and keep track of score
                for (int pipeNum = 0; pipeNum < pipesList.size(); pipeNum++)
                    {   
                       Sprite pipe = pipesList.get(pipeNum);
                       if (bird.overlaps(pipe))
                       {
                           Sprite gameOver = new Sprite();
                           gameOver.setImage("flapimages/gameover.png");
                           gameOver.position.set(200,300);
                           gameOver.render(context);
                           this.stop();
                           
                       }
                       
                       //Cannot use == here because frames issue
                       //Approximate when the bird passes pipes
                       // Only add 0.5 because the pipes come in pairs
                       if(nearEqual(bird.position.x,pipe.position.x,0.01))
                       {
                           score =  score + 0.5;
                        }
                       
                   }
                
                // Use this to make score sprites
                int scoreTens = (int) (score/10);
                int scoreOnes = (int)score - scoreTens*10;
                
                // tens digit
                if(scoreTens > 0){
                    
                    Sprite tens = new Sprite();
                    tens.setImage("flapimages/"+scoreTens+".png");
                    tens.position.set(180,50);
                    tens.render(context);
                    Sprite ones = new Sprite();
                    ones.setImage("flapimages/"+scoreOnes+".png");
                    ones.position.set(200,50);
                    ones.render(context);
                }
                else{
                    Sprite ones = new Sprite();
                    ones.setImage("flapimages/"+scoreOnes+".png");
                    ones.position.set(190,50);
                    ones.render(context);
                }
                            
            }
        };
        
        
        // Click screen (tap) to start
        mainScene.setOnMouseClicked(
            (MouseEvent event) ->
            {
                gameLoop.start();
            }
        );
        
        //Menu setup
        MenuBar bar = new MenuBar();
        Menu options = new Menu("Options");
        MenuItem reset = new MenuItem("Reset");
        MenuItem instruction = new MenuItem("Instructions");        
        bar.getMenus().add(options);
        options.getItems().addAll(reset,instruction);       
        root.setTop(bar);
        
        // Reset game by placing back the bird and clear pipes list
        reset.setOnAction(
            (ActionEvent)->
            {
                
                bird.position.set(100,300);
                bird.velocity.setLength(1);
                bird.velocity.setAngle(90);
                //set cat angle              
                
                pipesList.removeAll(pipesList);
                score=0;
                wait.render(context);
            }
        );
        
        //Pop up instructions window, stop game. Continue the game when closed.
        instruction.setOnAction(
            (ActionEvent)->
            {
                gameLoop.stop();
                Alert guide = new Alert(AlertType.INFORMATION);
                guide.setTitle("Instructions");
                guide.setHeaderText(null);
                guide.setContentText("Left click the waiting screen to start. Tap Spacebar to make the bird jump "
                +"Keep passing through the gap between pipes and get a high score!");
                guide.showAndWait();
                gameLoop.start();
            }
        );
        mainStage.show();
    }
}