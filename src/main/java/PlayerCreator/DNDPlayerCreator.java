package PlayerCreator;

import java.sql.*;


//file extensions
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
public class DNDPlayerCreator extends Application
{
 Stage pStage;
 /** This contains all the text fields it is how we parse the text inputs*/
 private ArrayList<TextField> TFL;
 //private TextArea PlayerDescription;
 private Connection connection;
 private String url="jdbc:mysql://127.0.0.1:3306/?user=root";
 private String user="root";
 private String password="password";
 private int characterLimit=90;
 private Label labelSuccess;
 private int successCount=0;
 private int failCount=0;
 
 public DNDPlayerCreator()
 {}
 
 public DNDPlayerCreator(String urlIn,String userIn,String passwordIn)
 {
 url=urlIn;
 user=userIn;
 password=passwordIn;
 }
  /**
  *Initalizes the gui
  */
 public void start(Stage primaryStage) 
 {
    try
    {
    primaryStage.setTitle("DNDPlayerCreator");
    Class.forName("com.mysql.jdbc.Driver");
    connection = DriverManager.getConnection(url, user, password);
    Statement stmt = connection.createStatement();
    stmt.execute("USE DndEncounters");
    ResultSet rs = stmt.executeQuery("Select * from Player");
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
    
    TFL=new ArrayList<TextField>();
  
    
      primaryStage.setTitle("Player Creator");
      //gride pane for holding all the buttons and labels
      GridPane grid = new GridPane();
      grid.setPadding(new Insets(10, 10, 10, 10));
      grid.setVgap(5);
      grid.setHgap(5);
      //For the left side of the gui indices 0 and 1
     String[] labels = {"Name", "Level", "Character Sheet Link"};
      for (int i = 0; i < labels.length; i++)
      {
          Label label = new Label(labels[i] + ":");
          TextField textField = new TextField();
          TFL.add(textField);
          grid.add(label, 0, i);
          grid.add(textField, 1, i);
       }
  
       Button button = new Button("Create Player");
       grid.add(button, 1, labels.length+1);
       button.setOnAction(new CreatePlayer());
       
       //Label label = new Label("PlayerStatblock");
       //PlayerDescription=new TextArea();
       
       
       //grid.add(label, 0, labels.length+3);
      // grid.add(PlayerDescription, 1, labels.length+3);

       labelSuccess = new Label("Player Creation Success Will Appear Here");
       grid.add(labelSuccess, 1, labels.length+4);
       
       // Set the scene and show the stage
       Scene scene = new Scene(grid, 800, 500);
       primaryStage.setScene(scene);
       primaryStage.show();
    }
    
 /**Parses text to remove sql injections*/
 private String ParseText(String text)
 {
 String temp="";
 String temp1;
 for(int i=0;i<text.length();i++)
 {
    temp1=text.substring(i,i+1);
    if(!temp1.equals(";")&&!temp1.equals("'")&&!temp1.equals(","))
    {
    temp+=temp1;
    }
 }
 
 return temp;
 
 }
 
 
 //To Do add a function to add a new line if a string goes on for a while without a new line make sure that the new lines add up with the spaces
 //Thought process go to your max length then go backwards until you find a space then insert the new line there
 
 //split it by new lines 
 //if a new line is longer then character limit go into while loop
 private String limitStringLength(String text)
 {
     String[] lines = text.split("\\r?\\n");
     String result = "";
 
     for (String line : lines) 
     {
         int i = characterLimit;
         int temp = 0;
 
         while (temp + i < line.length()) {
             if (line.charAt(temp + i) == ' '||line.charAt(temp + i)=='.') {
                 result += line.substring(0, temp + i+1) + "\n";
                 line = line.substring(temp + i + 1);
                 temp =0;
                 i = characterLimit;
             }
             else 
             {
                 i--;
                 if (i < 0) 
                 { 
                     temp += characterLimit;
                     i = characterLimit;
                 }
             }
         }
         result += line + "\n";
     }
     return result;
 }
 
 
 
 
 /**Pareses an integer and returns -1 if failed*/
 private int ParseTextInt(String text)
 {
    int num;
    try {
        num = Integer.parseInt(text);
    } 
    catch (NumberFormatException e) {
        //System.out.println(text + " cannot be converted to int");
        num = -1; // error value
    }
 return num;
 }
 
 /**Parses an double and returns -1 if failed*/
 private double ParseTextDouble(String text)
 {
    double num;
    try {
        num = Double.parseDouble(text);
    } 
    catch (NumberFormatException e) {
        //System.out.println(text + " cannot be converted to int");
        num = -1; // error value
    }
 return num;
 }
 
 
 
 private void printResultSet(ResultSet rs)
 {
    try
    {
       ResultSetMetaData rsmd = rs.getMetaData();
       int columnsNumber = rsmd.getColumnCount();
       
       // Iterate over the ResultSet, moving to the next row with each iteration
       while (rs.next())
          {
             // Iterate over the columns in the row
             for (int i = 1; i <= columnsNumber; i++) 
             {
                //if (i > 1) System.out.print(",  ");
                String columnValue = rs.getString(i);
                //System.out.print(columnValue + " " + rsmd.getColumnName(i));
             }
          //System.out.println("");
       }
     }
     catch(Exception e)
     {System.out.println("Print Failed");}
 }
 
 private int addToSQL(String[] stringArray)
 {
       for(int i=0;i<stringArray.length;i++)
       {
          if(stringArray[i].equals(""))
          {return-1;}
       }
       
       String PName;
       String CharacterSheetLink;
       String Level;   

       //do respective sql querries
       int i=0;
       PName=stringArray[i].toLowerCase();i++;
       PName = PName.substring(0, 1).toUpperCase() + PName.substring(1);
       
       Level=stringArray[i].toLowerCase();i++;
       Level = Level.substring(0, 1).toUpperCase() + Level.substring(1);
       
       CharacterSheetLink=stringArray[i].toLowerCase();i++;
       CharacterSheetLink = CharacterSheetLink.substring(0, 1).toUpperCase() + CharacterSheetLink.substring(1);
       
       //   Sourcebook=stringArray[i].toLowerCase();i++;
      //   Sourcebook = Sourcebook.substring(0, 1).toUpperCase() + Sourcebook.substring(1);
       
      // PName=stringArray[i];i++;
      //  CharacterSheetLink=stringArray[i];i++;
      // Level=stringArray[i];i++;
      
       
       String insert="Insert into Player(PName, CharacterSheetLink, Level)" +
       "Values('"+PName+"','"+CharacterSheetLink+"','"+Level+ "');";
       try
       {
          System.out.println(insert);
          Statement stmt = connection.createStatement();
          stmt.executeUpdate(insert);
          ResultSet rs = stmt.executeQuery("Select * from Player");
          printResultSet(rs);
          successCount++;
          labelSuccess.setText("Player Created Successfully # "+ successCount);
       }
       catch(Exception e)
       {
          failCount++;
          labelSuccess.setText("Player Creation Failed # "+failCount);
          e.printStackTrace();
          return -1;
       }
       
       //save the sql command into a file of sql commands incase of need to rewrite database
       try {
          // Open given file in append mode.
          BufferedWriter out = new BufferedWriter(new FileWriter("Player.txt", true));
          out.write(insert);
          out.write("\n\n");
          out.close();
      }
      catch (IOException e) {
          System.out.println("exception occurred" + e);
      }
       
      
    return 0;
 }
       
 
 /**Handles the logic for checking that the inputs were valid*/
 private class CreatePlayer implements EventHandler<ActionEvent> 
 {
      /**
       * handle Override the abstract method handle()
       */
    public void handle(ActionEvent event) 
    {
       
       //Order
       //Name,level, charactersheetlink
       String[] info = new String[3];
       int allFieldsSuccessful=0;
       try
       {
         //name
         info[0]=ParseText(TFL.get(0).getText());
         //level
         info[1]=""+ParseTextInt(TFL.get(1).getText());
         //character Sheet Link
         info[2]=ParseText(TFL.get(2).getText());
         //info[3]=limitStringLength(ParseText(PlayerDescription.getText()));
       }catch(Exception e)
       {allFieldsSuccessful=1;labelSuccess.setText("Monster Creation Failed # ");}
       
       
       
    	   
       
              
       //This is the player statblock 

       

       if(allFieldsSuccessful==0)
       {
       int temp=addToSQL(info);
       if(temp==0)
       {System.out.println("Success");}
       else
       {System.out.println("Failed");}
       //if all fields were successful add to sql 
       }
       //if it does not work dont worry
    }
    
 }


}
