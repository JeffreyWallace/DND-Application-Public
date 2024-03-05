package MonsterCreator;
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
public class DNDMonsterCreator extends Application
{
   Stage pStage;
   /** This contains all the text fields it is how we parse the text inputs*/
   private ArrayList<TextField> TFL;
   private TextArea MonsterDescription;
   private ComboBox<String> alignmentComboBox;
   private Connection connection;
   private String url="jdbc:mysql://127.0.0.1:3306/?user=root";
   private String user="root";
   private String password="";
   private int characterLimit=90;
   private Label labelSuccess;
   private int successCount=0;
   private int failCount=0;
   
   
   public DNDMonsterCreator()
   {}
   
   public DNDMonsterCreator(String urlIn,String userIn,String passwordIn)
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
      primaryStage.setTitle("DNDMonsterCreator");
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection(url, user, password);
      Statement stmt = connection.createStatement();
      stmt.execute("USE DndEncounters");
      ResultSet rs = stmt.executeQuery("Select * from Monster");
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
    
      
        primaryStage.setTitle("Monster Creator");
        //gride pane for holding all the buttons and labels
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        //For the left side of the gui indices 0 and 1
       String[] labels = { "Creature Type", "Sourcebook"};
        for (int i = 0; i < labels.length; i++)
        {
            Label label = new Label(labels[i] + ":");
            TextField textField = new TextField();
            TFL.add(textField);
            grid.add(label, 0, i);
            grid.add(textField, 1, i);
         }
         //For alignment
         Label alignmentLabel = new Label("Alignment:");
         alignmentComboBox = new ComboBox<>();
         alignmentComboBox.getItems().addAll("Lawful Good", "Neutral Good", "Chaotic Good", "Lawful Neutral", "True Neutral", "Chaotic Neutral", "Lawful Evil", "Neutral Evil", "Chaotic Evil","Unaligned");
         grid.add(alignmentLabel, 0, labels.length);
         grid.add(alignmentComboBox, 1, labels.length);
         

         
         Button button = new Button("Create Monster");
         grid.add(button, 1, labels.length+1);
         button.setOnAction(new CreateMonster());
         
         Label label = new Label("MonsterStatblock");
         MonsterDescription=new TextArea();
         grid.add(label, 0, labels.length+3);
         grid.add(MonsterDescription, 1, labels.length+3);
         labelSuccess = new Label("Creation Success will appear Here");
         grid.add(labelSuccess, 1, labels.length+4);

         
        //For the Middle/right of the gui indices 2 and 3
        /*
         String[] AbilityScores = {"Strength", "Dexterity", "Constitution", "Intelligence", "Wisdom",  "Charisma"};
         for (int i = 0; i < AbilityScores.length; i++) 
         {
            Label label1 = new Label(AbilityScores[i] + ":");
            TextField textField = new TextField();
            TFL.add(textField);
            grid.add(label1, 2, i);
            grid.add(textField, 3, i);
           
         }*/
         
         
 
         
         //add damage resistances etc seperate each by commas
           
         //add condition immunites seperate by commas.
         // Add a button to create the monster
         
         
         // Add a text area to display the created monster
           
         
         // Set the scene and show the stage
         Scene scene = new Scene(grid, 800, 500);
         primaryStage.setScene(scene);
         primaryStage.show();
      }
      
   /**Pareses text to remove sql injections*/
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
   
   /**Pareses an double and returns -1 if failed*/
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
         String monsterName;
         String CreatureType;
         String Size;
         String ChallengeRating;
         String Alignment;
         String Sourcebook;
         String Experience;
         String Hitpoints;
         String ArmorClass;
         String Strength;
         String Dexterity;
         String Constitution;
         String Intelligence;
         String Wisdom;
         String Charisma;
         String Statblock;
         //Order
         //Name,size,type,Sourcebook.hp.cr,xp,Armorclass,str,dex,con,wis,int,cha,statblock,alignment
         //do respective sql querries
         int i=0;
         
         
         CreatureType=stringArray[i].toLowerCase();i++;
         CreatureType = CreatureType.substring(0, 1).toUpperCase() + CreatureType.substring(1);
         CreatureType = CreatureType.replace(" ", "");
         Sourcebook=stringArray[i].toLowerCase();i++;
         Sourcebook = Sourcebook.substring(0, 1).toUpperCase() + Sourcebook.substring(1);
         
         Statblock=stringArray[i];i++;
         Alignment=stringArray[i];i++;
         
         
         
         String tempStatblockLower=Statblock.toLowerCase();
         //Parse Name From statblock
         monsterName=Statblock.substring(0,Statblock.indexOf("\n")).toLowerCase();
         monsterName = monsterName.substring(0, 1).toUpperCase() + monsterName.substring(1);
         //Parse Size from the Statblock
         String tempStatblock=Statblock.substring(Statblock.indexOf("\n")+1);
         Size=tempStatblock.substring(0,tempStatblock.indexOf(" "));
         Size = Size.substring(0, 1).toUpperCase() + Size.substring(1).toLowerCase();
         
         //parse HP from text
         tempStatblock=Statblock.substring(tempStatblockLower.indexOf("points")+6);
         Hitpoints=tempStatblock.substring(0,tempStatblock.indexOf("("));
         Hitpoints = Hitpoints.replace(" ", "");
         
         //parse CR from text
         tempStatblock=Statblock.substring(tempStatblockLower.indexOf("challenge ")+9);
         ChallengeRating=tempStatblock.substring(0,tempStatblock.indexOf("("));
         ChallengeRating = ChallengeRating.replace(" ", "");
         
         //parse XP from text
         Experience=tempStatblock.substring(tempStatblock.indexOf("(")+1,tempStatblock.indexOf(")")).toLowerCase();
         Experience = Experience.replace(" ", "");
         Experience = Experience.replace("xp", "");
         
         //parse AC from text
         tempStatblock=Statblock.substring(Statblock.indexOf("Class")+6);
         tempStatblock=tempStatblock.substring(0,tempStatblock.indexOf("H"));
         ArmorClass=tempStatblock;
         try
         {
         ArmorClass=tempStatblock.substring(0,tempStatblock.indexOf("("));
         }
         catch(Exception e)
         {}
         ArmorClass = ArmorClass.replace(" ", "");
         
         
         
         
         
         //makes a string from STR to Cha +6
         tempStatblock=Statblock.toUpperCase();
         String temp=tempStatblock.substring(tempStatblock.indexOf("\nSTR"));
         tempStatblock=tempStatblock.substring(tempStatblock.indexOf("\nSTR"),tempStatblock.indexOf("\nSTR")+temp.indexOf("CHA")+30);
         //System.out.print(tempStatblock);
         //removes all the space 
         tempStatblock = tempStatblock.replace(" ", "");
         
         //After this take the string from STRto ( Then Remove everything before the next stat
         Strength=tempStatblock.substring(tempStatblock.indexOf("STR")+3,tempStatblock.indexOf("("));
         tempStatblock=tempStatblock.substring(tempStatblock.indexOf("DEX"));
         Dexterity=tempStatblock.substring(tempStatblock.indexOf("DEX")+3,tempStatblock.indexOf("("));
         tempStatblock=tempStatblock.substring(tempStatblock.indexOf("CON"));
         Constitution=tempStatblock.substring(tempStatblock.indexOf("CON")+3,tempStatblock.indexOf("("));
         tempStatblock=tempStatblock.substring(tempStatblock.indexOf("INT"));
         Intelligence=tempStatblock.substring(tempStatblock.indexOf("INT")+3,tempStatblock.indexOf("("));
         tempStatblock=tempStatblock.substring(tempStatblock.indexOf("WIS"));
         Wisdom=tempStatblock.substring(tempStatblock.indexOf("WIS")+3,tempStatblock.indexOf("("));
         tempStatblock=tempStatblock.substring(tempStatblock.indexOf("CHA"));
         Charisma=tempStatblock.substring(tempStatblock.indexOf("CHA")+3,tempStatblock.indexOf("("));
         
         String insert="Insert into Monster(MonsterName, CreatureType, Size, ChallengeRating, Alignment, Sourcebook, Experience, HitPoints, ArmorClass, Strength, Dexterity,Constitution, Intelligence, Wisdom,Charisma, Statblock)"+
         "Values('"+monsterName+"','"+CreatureType+"','"+Size+"',"+ChallengeRating+",'"+Alignment+"','"+Sourcebook+"',"+Experience+","+Hitpoints+","+ArmorClass+","+Strength+","+Dexterity+","+Constitution+","+Intelligence+","+Wisdom+","+Charisma+",'"+Statblock+"');";
         System.out.println(insert);
         try
         {
         Statement stmt = connection.createStatement();
         stmt.executeUpdate(insert);
         ResultSet rs = stmt.executeQuery("Select * from Monster");
         printResultSet(rs);
         successCount++;
         labelSuccess.setText("Monster Created Successfully # "+ successCount);
         }
         catch(Exception e)
         {
         failCount++;
         labelSuccess.setText("Monster Creation Failed # "+failCount);
         e.printStackTrace();
         return -1;
         }
         //save the sql command into a file of sql commands incase of need to rewrite database
         try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(new FileWriter(Sourcebook+".txt", true));
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
   private class CreateMonster implements EventHandler<ActionEvent> 
   {
        /**
         * handle Override the abstract method handle()
         */
      public void handle(ActionEvent event) 
      {
         
         //Order
         //Name,size,type,Sourcebook.hp.cr,xp,Armorclass,str,dex,con,int,wis,cha
         String[] stats=new String[4];
      
      
         int allFieldsSuccessful=0;
         
         
         
         //Retrieves the first 4 which are name size type and SourceBook
         for(int i=0;i<TFL.size();i++)
         {
            if(i<2)
            {
            //System.out.println("This line is "+ParseText(TFL.get(i).getText()));
            stats[i]=ParseText(TFL.get(i).getText());
            }
                  
         //add functions that check if an integer is valid
         //add function to check that there is no sql keywords invloved and that the string is not longer then 255
         }
         
         //This is the monster statblock 
         //System.out.println("This line is "+ParseText(MonsterDescription.getText()));
         stats[TFL.size()]=limitStringLength(ParseText(MonsterDescription.getText()));
         try{
         //System.out.println("This line is "+ParseText(alignmentComboBox.getValue()));
         stats[TFL.size()+1]=alignmentComboBox.getValue();
         }
         catch(Exception e)
         {
            allFieldsSuccessful++;
         }

         if(allFieldsSuccessful==0)
         {
         int temp=addToSQL(stats);
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