package RemoveEncounter;
import java.sql.*;
import java.io.*;
import java.nio.file.*;
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

import DNDEncounterLauncher.DNDEncounterLauncher;

public class DNDRemoveEncounter extends Application
{
   private  String url = "";
   private  String user = "";
   private  String password = "";
   private  String driver="";
   private  String encounterToRemove="";
   private Stage pStage;
   private TextField pathToFileTF;
   private Label confirmRemove;
   
   
   public DNDRemoveEncounter(String urlIn,String userIn,String passwordIn,String driverIn,String encounterToRemoveIn)
   {
      url=urlIn;
      user=userIn;
      password=passwordIn;
      driver=driverIn;
      encounterToRemove=encounterToRemoveIn;
   }
   
   public void start(Stage primaryStage) 
   {
         pStage=primaryStage;
         primaryStage.setTitle("DNDConfrimRemove");
         int width=300;
         int height=300;
         
         Button Remove=new Button("Remove");
         Button Cancel=new Button("Cancel");
         Remove.setOnAction(new remove());
         Cancel.setOnAction(new Cancel());
         confirmRemove=new Label("Are you sure you want to remove "+encounterToRemove);
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

   
   private class remove implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent event)
      {
         try
         {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement stmt = connection.createStatement();
            stmt.execute("USE DndEncounters");
            stmt.executeUpdate("DELETE FROM Encounter WHERE EncounterName = '" + encounterToRemove + "'");
            Path p =Paths.get(encounterToRemove+".txt");
            Files.delete(p);
            closeApplication();
         }
         catch(Exception e)
         {System.out.println("error Detected in remove");e.printStackTrace();}
         
      }
   }
   private class Cancel implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent event)
      {
         closeApplication();
         
      }
   }
}