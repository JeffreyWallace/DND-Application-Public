package ConfirmOverwrite;
import java.sql.*;
import java.io.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

//to send data backwards
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import DNDEncounterLauncher.DNDEncounterLauncher;

public class DNDOverwriteEncounter extends Application
{
   private  String url = "";
   private  String user = "";
   private  String password = "";
   private  String driver="";
   private  String encounterToOverwrite="";
   private Stage pStage;
   private TextField pathToFileTF;
   private Label confirmRemove;
   
   private StringProperty data = new SimpleStringProperty();

    // getter and setter for data
    public String getData() {
        return data.get();
    }

    public void setData(String data) {
        this.data.set(data);
    }

    public StringProperty dataProperty() {
        return data;
    }
   
   public DNDOverwriteEncounter(String urlIn,String userIn,String passwordIn,String driverIn,String overwrite)
   {
      url=urlIn;
      user=userIn;
      password=passwordIn;
      driver=driverIn;
      encounterToOverwrite=overwrite;
   }
   
   public void start(Stage primaryStage) 
   {
         pStage=primaryStage;
         primaryStage.setTitle("DNDConfrimRemove");
         int width=300;
         int height=300;
         
         Button Remove=new Button("Overwrite");
         Button Cancel=new Button("Cancel");
         Remove.setOnAction(new confirmOverwrite());
         Cancel.setOnAction(new CancelOverwrite());
         confirmRemove=new Label("Are you sure you want to overwrite "+encounterToOverwrite);
         GridPane gp=new GridPane();
         gp.add(Remove,0,0);
         gp.add(Cancel,1,0);
         VBox root=new VBox(confirmRemove,gp);
         Scene scene = new Scene(root, width, height);
         primaryStage.setScene(scene);
         primaryStage.show();
   }
   private void closeApplication()
   {
   pStage.close();
   }  

   
   private class confirmOverwrite implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent event)
      {
        //send yes
        setData("yes");
        closeApplication();
      }
   }
   private class CancelOverwrite implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent event)
      {
         //send no
         setData("no");
         closeApplication();
         
      }
   }
}