
package DND_Weather_Generator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;

import java.util.Random;

/** This class controls The Graphical User Interface of the DND Encounter Builder*/
public class DNDWeatherGenerator extends Application
{
   Stage pStage;
   TextField searchBar;
   Button weatherButton;
   private  Label lbl = new Label("");
    /**
     
    */
   public void start(Stage primaryStage) 
   {
      pStage=primaryStage;
      weatherButton =new Button("Generate Weather");
      lbl=new Label("");
      GridPane gridpane = new GridPane();
      //gridpane.setAlignment(Pos.LEFT);
      weatherButton.setOnAction(new ButtonHandler());
      gridpane.add(lbl,0,1);
      gridpane.add(weatherButton,0,0);
        
      VBox root = new VBox(gridpane);
      Scene scene = new Scene(root, 200, 100);
      pStage.setScene(scene);
      pStage.show();
   }
   private class ButtonHandler implements EventHandler<ActionEvent> 
   {
        /**
         * handle Override the abstract method handle()
         */
      public void handle(ActionEvent event) 
      {
         String str=" ";
         Random rand=new Random();
         int Temprature=rand.nextInt(20)+1;
         if(Temprature<=14)
         {
         str+="Temprature Normal ";
         }
         else if(Temprature<=17)
         {
         Temprature=rand.nextInt(4)+1;
         str+="Temprature "+(Temprature*10)+" degrees colder ";
         }
         else if(Temprature<=20)
         {
         Temprature=rand.nextInt(4)+1;
         str+="Temprature "+(Temprature*10)+" degrees warmer ";
         }
         str+="\n ";
         
        int wind=rand.nextInt(20)+1;
        if(wind<=12)
        {
        str+="No wind ";
        }
        else if(wind<=17)
        {
        str+="Light wind ";
        }
        else if(wind<=20)
        {
        str+="Strong wind ";
        }
        str+="\n ";
         
        int rain=rand.nextInt(20)+1;
        if(rain<=12)
         
         str+="No rain ";
         
        else if(rain<=17)
        {
         str+="Light rain ";
        }
        else if(rain<=20)
        {
        str+="Strong rain ";
        }
        lbl.setText(str);
      }
   }

}