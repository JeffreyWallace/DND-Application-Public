package DND_HP_Program;
import DND_Weather_Generator.DNDWeatherGenerator;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.stage.Stage;
import javafx.scene.Scene;
import java.util.ArrayList;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.event.ActionEvent; //**Need to import
import javafx.event.EventHandler;


public class DNDHPLauncher extends Application 
{
   
   private ArrayList<Integer> HPL;
   private ArrayList<Button> HPT,ResetB;
   private ArrayList<Label> HPLBL;
   private ArrayList<TextField> TFL;
   private int Buttonamount=15;
   public void start(Stage stage)
   {
      
      HPL =new ArrayList<Integer>();
      HPLBL = new ArrayList<Label>();
      HPT =new ArrayList<Button>();
      ResetB=new ArrayList<Button>();
      TFL=new ArrayList<TextField>();
      GridPane gridpane =new GridPane();
      gridpane.setAlignment(Pos.CENTER);
      gridpane.setPadding(new Insets(11, 12, 13, 14));
      gridpane.setHgap(10);
      gridpane.setVgap(5);
      
      
      
      
      for(int i=1;i<Buttonamount+1;i++)
      {
         HPL.add(i-1,0);
         HPT.add(i-1,new Button("Add HP"));
         ResetB.add(i-1,new Button("Reset"));
         HPLBL.add(i-1,new Label("HP Total: "+HPL.get(i-1)));
         TFL.add(i-1,new TextField());
         
         
         gridpane.add(new Label("Monster"+i),0,i-1);
         gridpane.add(TFL.get(i-1),1,i-1);
         gridpane.add(HPT.get(i-1),2,i-1);
         gridpane.add(HPLBL.get(i-1),3,i-1);
         gridpane.add(ResetB.get(i-1),4,i-1);
         HPT.get(i-1).setOnAction(new ButtonHandler());
         ResetB.get(i-1).setOnAction(new ButtonHandler());
      }
   
      Scene scene = new Scene(gridpane, 500, 500);
      stage.setTitle("DND Monster HP Tracker");
      stage.setScene(scene);
      stage.show();
   }
   private class ButtonHandler implements EventHandler<ActionEvent> 
   {
        /**
         * handle Override the abstract method handle()
         */
      public void handle(ActionEvent event) 
      {
         Object source = event.getSource();
         //System.out.println("hello");
         for(int i=0;i<Buttonamount;i++)
         {
            if(source==HPT.get(i))
            {
               try{
                    HPL.set(i,(Integer.parseInt(TFL.get(i).getText()))+HPL.get(i));
                    HPLBL.get(i).setText("HP Total: "+HPL.get(i));
                    TFL.get(i).setText("");
                 }
               catch(Exception e)
               {
                  System.out.println("Error Caught");
               }
            }
            else if(source==ResetB.get(i))
            {
              HPL.set(i,0);
              HPLBL.get(i).setText("HP Total: "+HPL.get(i));
            }
         }
      }
   }
   public static void main(String[] args) {
      
        launch(args);
    }

}
