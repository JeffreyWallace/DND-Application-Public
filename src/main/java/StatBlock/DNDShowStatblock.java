package StatBlock;
import java.sql.*;


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
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;

/** This class controls The Graphical User Interface of the DND Encounter Builder*/
public class DNDShowStatblock extends Application
{
   Stage pStage;
   /** This contains all the text fields it is how we parse the text inputs*/
   
   private TextArea MonsterDescription;
   
   private Connection connection;
   private String url="jdbc:mysql://127.0.0.1:3306/?user=root";
   private String user="root";
   private String password="";
   private String monsterName;
   
   private int height=800;
   private int width=510;
   
   
   public DNDShowStatblock()
   {}
   
   public DNDShowStatblock(String urlIn,String userIn,String passwordIn,String mName)
   {
   url=urlIn;
   user=userIn;
   password=passwordIn;
   monsterName=mName;
   }
    /**
    *Initalizes the gui
    */
   public void start(Stage primaryStage) 
   {
      try
      {
      
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection(url, user, password);
      Statement stmt = connection.createStatement();
      stmt.execute("USE DndEncounters");
      ResultSet rs = stmt.executeQuery("Select Statblock from Monster where MonsterName='"+monsterName+"'");
      rs.next();
      String statBlock = rs.getString(1);
      MonsterDescription=new TextArea(statBlock);
      MonsterDescription.setPrefHeight(height);
      MonsterDescription.setStyle("-fx-control-inner-background: #f6efd1;");
      primaryStage.setTitle("DND "+monsterName+" Statblock");


      //printResultSet(rs);
      }
      catch(ClassNotFoundException e)
      {
         System.out.println("MySQL JDBC Driver not found. Add it to your library path ");
         e.printStackTrace();
         
      }
      catch(SQLException e)
      {
         System.out.println("Could not connect to the database. Check your URL, username, and password");
         e.printStackTrace();
      }
      catch(Exception e)
      {
         System.out.println("Unknown exception");
         e.printStackTrace();
      }
         VBox root=new VBox(MonsterDescription);
         root.setStyle("-fx-background-color: #f6efd1;");
      
         Scene scene = new Scene(root, width, height);
         primaryStage.setScene(scene);
         primaryStage.show();
      }
      
      

   
   
         

   

}