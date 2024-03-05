package DNDSQLLauncher;
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

import DNDEncounterLauncher.DNDEncounterLauncher;

public class DNDSQLApplication extends Application
{
   private  String url = "";
   private  String user = "";
   private  String password = "";
   private  String driver="";
   private  String pathToFile = "path_to_your_file";
   
   private Stage pStage;
   
   private TextField urlTF,userTF,passwordTF;
   private Label testStatus;
   
   
   public static void main(String[] args) {
        launch(args);
        
    }
   
   public void start(Stage primaryStage) 
   {
         int width=300;
         int height=300;
         pStage=primaryStage;
         pathToFile="DNDStartup.txt";
         //if file exists open that file and close connecitons
         File startupFile=new File("DNDStartup.txt");
         if(startupFile.exists())
         {
            try{
               BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
               if(reader.readLine().equals("DataSaved"))
               {
                  loadFiles();
               }
            }
            catch(Exception e)
            {
            System.out.print("error loading read file");
            e.printStackTrace();
            pStage.close();
            }
         }
         else
         {
            urlTF=new TextField("Enter URL");
            userTF=new TextField("Enter User");
            passwordTF=new TextField("Enter Password");
            Button testConnection=new Button("Connect");
            testConnection.setOnAction(new create());
            testStatus=new Label("Status");
            VBox root=new VBox(urlTF,userTF,passwordTF,testStatus,testConnection);
            Scene scene = new Scene(root, width, height);
            primaryStage.setScene(scene);
            primaryStage.show();
         }
   }
   
   
   
   
   
   public void loadFiles()
   {
      
      try 
      {
         BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
         reader.readLine(); //used to skip the datasaved line
         url = reader.readLine();
         user = reader.readLine();
         password = reader.readLine();
         driver = "com.mysql.cj.jdbc.Driver";
         reader.close();
      } 
      catch (Exception e) 
      {
         System.out.println("Error reading file");
         testStatus.setText("Error reading file");
         e.printStackTrace();
         return;
      }
      Connection connection = null;
      try 
      {
         connection = DriverManager.getConnection(url, user, password);
         if (connection != null) 
         {
         //System.out.println("Connected to the database successfully!");
         if(testStatus!=null)
         {testStatus.setText("Connected to the database successfully!");}
         }
      } 
      catch (SQLException e) 
      {
         System.out.println("Failed to create database connection");
         if(testStatus!=null)
         {testStatus.setText("Failed to create database connection");}
         e.printStackTrace();
      } 
      finally 
      {
         if (connection != null) 
         {
            try 
            {
               connection.close();
               DNDEncounterLauncher DEL=new DNDEncounterLauncher(url,user,password,driver);
               Stage stage2 = new Stage();
               DEL.start(stage2);
               pStage.close();
            }
            catch (SQLException e) 
            {
               System.out.println("Failed to close the connection");
               if(testStatus!=null)
               {testStatus.setText("Failed to close the connection");}
               e.printStackTrace();
            }
         }
      }
   }
   
   private class create implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent event)
      {
         try{
         BufferedWriter out = new BufferedWriter(new FileWriter(pathToFile,false));
         out.write("DataSaved\n");
         out.write(urlTF.getText()+"\n");
         out.write(userTF.getText()+"\n");
         out.write(passwordTF.getText());
         out.close();
         }
         catch(Exception e)
         {
         testStatus.setText("File creation Failed");
         }
         loadFiles();
      }
   }

}