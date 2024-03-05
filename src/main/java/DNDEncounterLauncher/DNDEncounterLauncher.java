package DNDEncounterLauncher;
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
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.geometry.Insets;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


import java.util.ArrayList;

import DNDEncounterBuilder.DNDEncounterBuilder;
import RemoveEncounter.DNDRemoveEncounter;

public class DNDEncounterLauncher extends Application
{
   private  String url = "";
   private  String user = "";
   private  String password = "";
   private  String driver="";
   
   
   
   private Connection connection;
   private TextField searchBar;
   private ScrollPane encounterScrollPane;
   private GridPane EncounterGridPane;
   private Button createNewEncounter;
   private ArrayList<Button> editEncounterBL,removeEncounterBL;
   private ArrayList<String> encounterNamesLst;

   public DNDEncounterLauncher(String urlIn,String userIn,String passwordIn,String driverIn)
   {
   url=urlIn;
   user=userIn;
   password=passwordIn;
   driver=driverIn;
   }
   
  
   
   public void start(Stage primaryStage) 
   {
         
           
         
         
         
         int width=800;
         int height=400;
         primaryStage.setTitle("DNDEncounterViewer");
         
         searchBar=new TextField("Search for an encounter here");
         editEncounterBL=new ArrayList();
         EncounterGridPane=new GridPane();
         
         encounterScrollPane=new ScrollPane();
         encounterScrollPane.setContent(EncounterGridPane);
         encounterScrollPane.setPrefSize(width*2/3,height/2);
         
         createNewEncounter=new Button("Create new Encounter");
         createNewEncounter.setOnAction(new createNewEncounterHandler());
         
         Button updateDisplay=new Button("Update display");
         updateDisplay.setOnAction(new updateDisplay());
         
         
         VBox root=new VBox(updateDisplay,searchBar,encounterScrollPane,createNewEncounter);
         Scene scene = new Scene(root, width, height);
         primaryStage.setScene(scene);
         primaryStage.show();
         
         
         try
         {
            
            connection = DriverManager.getConnection(url, user, password);
            Statement stmt = connection.createStatement();
            stmt.execute("USE DndEncounters");

         
            ResultSet rs = stmt.executeQuery("Select * from encounter");
            displayEncounters(rs);
            
         }
         catch(Exception e)
         {
            System.out.println("Error occured in updating display");e.printStackTrace();
         }
         
         
         
         
   }
   private void displayEncounters(ResultSet rs)
   {
   try{
         removeEncounterBL=new ArrayList();
         encounterNamesLst=new ArrayList();
         editEncounterBL=new ArrayList();
         EncounterGridPane=new GridPane();
         EncounterGridPane.setStyle("-fx-background-color: white;");
         
         ResultSetMetaData rsmd = rs.getMetaData();
         int columnsNumber = rsmd.getColumnCount();
         String header="";
         
         String strTotal="";
         //Adds The Header label and a space afterwards
         for (int i = 1; i <= columnsNumber; i++) 
         {
         
            
            header=rsmd.getColumnName(i);
            while(header.length()-20<0)
            {
               header+=" ";
            }
            
            
            
            strTotal+=header;
         }
         
         
         //header label
         
         Label headLbl=new Label(strTotal);
         headLbl.setPadding(new Insets(10, 10, 10, 10));
         headLbl.setFont(Font.font("monospaced", 11));
         EncounterGridPane.add(headLbl,0,0);
         
         //Space
         Label spaceLbl=new Label("");
         EncounterGridPane.add(spaceLbl,0,1);
         
         
         //used to place the Labels in the correct height of the gridpane
         int rsCount=0;
         // Iterate over the ResultSet, moving to the next row with each iteration
         while (rs.next())
         {
            // Iterate over the columns in the row
            //adds labes for each monster in the querry
            encounterNamesLst.add(rs.getString(1));
            String result="";
            for (int i = 1; i <= columnsNumber; i++) 
            {
               String columnValue = rs.getString(i);
               
               while(columnValue.length()<20)
               {
               columnValue+=" ";
               }
               
               result+=columnValue;
                                 
            }
            //Spacer Label
            Label spaceLbl2=new Label("");
            EncounterGridPane.add(spaceLbl2,0,rsCount*2+1);
            //Monster information Label
            Label lbl=new Label(result);
            lbl.setPadding(new Insets(10, 10, 10, 10));
            lbl.setFont(Font.font("monospaced", 11));
            EncounterGridPane.add(lbl,0,rsCount*2+2);
            
            //Buttons
            Button addToEncounter =new Button("Edit Encounter");
            addToEncounter.setOnAction(new editEncounter());
            EncounterGridPane.add(addToEncounter,1,rsCount*2+2);
            editEncounterBL.add(addToEncounter);
            
            //removeEncounterBL
            Button removeEncounter =new Button("Remove Encounter");
            removeEncounter.setOnAction(new removeEncounter());
            EncounterGridPane.add(removeEncounter,2,rsCount*2+2);
            removeEncounterBL.add(removeEncounter);
            
            
            //sets it so the display is update

            rsCount++;
            
            
         }

         EncounterGridPane.setMaxSize(600,300);
         encounterScrollPane.setContent(EncounterGridPane);
       }
       catch(Exception e)
       {System.out.println("Print Failed");e.printStackTrace();}
   }
   
   private class createNewEncounterHandler implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent event)
      {
         
         DNDEncounterBuilder DEB=new DNDEncounterBuilder(url,user,password,driver);
         Stage stage2 = new Stage();
         DEB.start(stage2);
      }
   }
   
   private class editEncounter implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent event)
      {
         Object source = event.getSource();
         for(int i=0;i<editEncounterBL.size();i++)
         {
            
            if(source==editEncounterBL.get(i))
            { 
            DNDEncounterBuilder DEB=new DNDEncounterBuilder(url,user,password,driver,encounterNamesLst.get(i));
            Stage stage2 = new Stage();
            DEB.start(stage2);
            
            }
         }
         
      }
   }
   private class removeEncounter implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent event)
      {
         Object source = event.getSource();
         for(int i=0;i<removeEncounterBL.size();i++)
         {
            
            if(source==removeEncounterBL.get(i))
            { 
            //change the code to make it open a new window asking if they are sure they want to remove the encounter
            System.out.println(encounterNamesLst.get(i));
            DNDRemoveEncounter DEB=new DNDRemoveEncounter(url,user,password,driver,encounterNamesLst.get(i));
            Stage stage2 = new Stage();
            DEB.start(stage2);
            
            }
         }
         
      }
   }
   private class updateDisplay implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent event)
      {
         
         try
         {
            
            connection = DriverManager.getConnection(url, user, password);
            Statement stmt = connection.createStatement();
            stmt.execute("USE DndEncounters");

         
            ResultSet rs = stmt.executeQuery("Select * from encounter");
            displayEncounters(rs);
            
         }
         catch(Exception e)
         {
            System.out.println("Error occured in updating display");e.printStackTrace();
         }
      }
   }
   
   
}