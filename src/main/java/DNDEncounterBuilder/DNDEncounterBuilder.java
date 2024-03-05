package DNDEncounterBuilder;
import java.sql.*;

import DownloadMonsterLibraries.DownloadMonsterLibrary;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.application.Platform;

import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.layout.HBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import StatBlock.DNDShowStatblock;
import MonsterCreator.DNDMonsterCreator;
import MonsterCreator.DNDMonsterCreatorSimple;
import ConfirmOverwrite.DNDOverwriteEncounter;

//used for confirming overwriting an encounter
import java.util.concurrent.CountDownLatch;

//used to remove files after deletion
import java.nio.file.*;



// to-DO
//change DNDShowStatblock to require a sourcebooks as well!
/** This class controls The Graphical User Interface of the DND Encounter Builder*/
public class DNDEncounterBuilder extends Application
{
   private String url="";
   private String user="";
   private String password="";
   private String driver="com.mysql.cj.jdbc.Driver";
   
   private String searchQuery,playSearchQuery="";
   private Connection connection;
   private VBox vBox;
   private GridPane addMonsterDisplay,gridpane,gridpaneFilters, monstersInEncounterGridpane;
   private ArrayList<String> monsterNamesLst,monstersInEncounterLst,encounterNamesUnqiue;
   private ArrayList<Integer> amountofMonsters;//is the amount of monsters for each unique name
   private int displayAmount=10;
   private HBox root;
   private ScrollPane DisplayscrollPane,DisplayscrollPane2,monsterInEncounterScrollPane;
   
   private ArrayList<Button> AddToEncounterBL,ShowStatblockBL,addMonsterBL,subtractMonsterBL,showStatblockEncounterBL;
   private Label EncounterDifficulty;
   private GridPane playersnEncounters;
   //used to update when enter is pressed
   private updateDisplay displayUpdater = new updateDisplay();
   
   
   //filters 
   private ComboBox<String> typeFilters,sizeFilters,statFilters,alignmentFilter,SourcebookFilter;
   private Stage pStage;
   private TextField searchBar,encounterName;
   private TextField playerAmount,playerLevel;
   private Button searchButton;
   private int screenWidth,screenHeight;
   private String encounterNameLoad="";
   private boolean editingEncounter=false;
   private TextField crHigh,crLow;
   
   int[][] encounterDifficulty = 
   {
      {25, 50, 75, 100},  // 1st
      {50, 100, 150, 200},  // 2nd
      {75, 150, 225, 400},  // 3rd
      {125, 250, 375, 500},  // 4th
      {250, 500, 750, 1100},  // 5th
      {300, 600, 900, 1400},  // 6th
      {350, 750, 1100, 1700},  // 7th
      {450, 900, 1400, 2100},  // 8th
      {550, 1100, 1600, 2400},  // 9th
      {600, 1200, 1900, 2800},  // 10th
      {800, 1600, 2400, 3600},  // 11th
      {1000, 2000, 3000, 4500},  // 12th
      {1100, 2200, 3400, 5100},  // 13th
      {1250, 2500, 3800, 5700},  // 14th
      {1400, 2800, 4300, 6400},  // 15th
      {1600, 3200, 4800, 7200},  // 16th
      {2000, 3900, 5900, 8800},  // 17th
      {2100, 4200, 6300, 9500},  // 18th
      {2400, 4900, 7300, 10900},  // 19th
      {2800, 5700, 8500, 12700}  // 20th
   }; 
   

   

   public static void main(String[] args) 
   {
      
      launch(args);
   }
   public DNDEncounterBuilder()
   {}
   public DNDEncounterBuilder(String urlIn,String userIn,String passwordIn,String driverIn)
   {
   url=urlIn;
   user=userIn;
   password=passwordIn;
   driver=driverIn;
   }
   public DNDEncounterBuilder(String urlIn,String userIn,String passwordIn,String driverIn,String encounterNameToLoad)
   {
   url=urlIn;
   user=userIn;
   password=passwordIn;
   driver=driverIn;
   
   encounterNameLoad=encounterNameToLoad;
   //write code here to process this encounterNameToLoad
   }
   public void start(Stage primaryStage) 
   {
      screenWidth=1550;
      screenHeight=800;
      
      /* code to be moved into new encounter viewer or startup
      //Code to read inputs from a file
      
      */
   
      pStage=primaryStage;
      pStage.setTitle("DNDEncounterBuilder");
      searchBar=new TextField("Filter Monster Names here");
      encounterName=new TextField("Enter the encounter name here");
      
      //code for when a user selects the search textfield to add and remove text automatically
      searchBar.setOnMouseClicked(event->{
         if(searchBar.getText().equals("Filter Monster Names here"))
         {
         searchBar.setText("");
         }
      });
      searchBar.focusedProperty().addListener((obs, oldVal, newVal) -> {
      if (!newVal) {
         if(searchBar.getText().equals(""))
         {
         searchBar.setText("Filter Monster Names here");
         }
      }
      });

      gridpane = new GridPane();
      //gridpane.setAlignment(Pos.LEFT);
      gridpane.add(encounterName,0,0);
      
      
      
      gridpane.add(searchBar,0,1);
            
      //filter options 
      typeFilters = new ComboBox<>();
      sizeFilters = new ComboBox<>();
      statFilters = new ComboBox<>();
      alignmentFilter = new ComboBox<>();
      SourcebookFilter = new ComboBox<>(); 
      typeFilters.getItems().addAll("None","Aberration", "Beast", "Celestial", "Construct", "Dragon", "Elemental", "Fey", "Fiend", "Giant", "Humanoid", "Monstrosity", "Ooze", "Plant", "Undead","Other");
      typeFilters.setValue("None");
      
      sizeFilters.getItems().addAll("None","Tiny", "Small", "Medium","Large","Huge","Gargantuan","Other");
      sizeFilters.setValue("None");
      
      statFilters.getItems().addAll("None","Strength", "Dexterity", "Constitution","Intelligence","Wisdom","Charisma");
      statFilters.setValue("None");
      
      alignmentFilter.getItems().addAll("None","Lawful Good", "Neutral Good", "Chaotic Good", "Lawful Neutral", "True Neutral", "Chaotic Neutral", "Lawful Evil", "Neutral Evil", "Chaotic Evil","Unaligned");
      alignmentFilter.setValue("None");

      vBox = new VBox();
      playersnEncounters=new GridPane();
      gridpaneFilters=new GridPane();
      monstersInEncounterGridpane = new GridPane();
      
      
      // Create labels
      Label typeLabel = new Label("Type Filter:");
      Label sizeLabel = new Label("Size Filter:");
      Label statLabel = new Label("Stat Filter:");
      Label alignmentLabel = new Label("Alignment Filter:");
      Label sourcebookLabel = new Label("Sourcebook Filter:");
      
      // Add labels and ComboBoxes to the GridPane
      gridpaneFilters.add(typeLabel, 0, 2);
      gridpaneFilters.add(typeFilters, 1, 2);
      
      gridpaneFilters.add(sizeLabel, 0, 3);
      gridpaneFilters.add(sizeFilters, 1, 3);
      
      gridpaneFilters.add(statLabel, 0, 4);
      gridpaneFilters.add(statFilters, 1, 4);
      
      gridpaneFilters.add(alignmentLabel, 0, 5);
      gridpaneFilters.add(alignmentFilter, 1, 5);
      
      gridpaneFilters.add(sourcebookLabel, 0, 6);
      gridpaneFilters.add(SourcebookFilter, 1, 6);
      
 
      //gridpane.add(gridpaneFilters,2,3);
            
      searchButton =new Button("Search");
      searchButton.setOnAction(new updateDisplay());
      gridpaneFilters.add(searchButton,0,1);
      
      //cr filter
      Label crL=new Label("CR Low");
      Label crH=new Label("CR High");
      gridpaneFilters.add(crL,0,8);
      gridpaneFilters.add(crH,0,9);
      
      
      crHigh=new TextField("30");
      crLow=new TextField("0");
      gridpaneFilters.add(crLow,1,8);
      gridpaneFilters.add(crHigh,1,9);
      
      
      
      Button createNewMonster=new Button("Create Monster");
      createNewMonster.setOnAction(new createMonster());
      gridpaneFilters.add(createNewMonster,0,10);
      Button createNewMonsterADV=new Button("Create Monster Advanced");
      createNewMonsterADV.setOnAction(new createMonsterADV());
      gridpaneFilters.add(createNewMonsterADV,0,11);
      

      
      
      Button createEncounter=new Button("Create Encounter");
      createEncounter.setOnAction(new createEncounter());
      gridpaneFilters.add(createEncounter,0,13);

      Button importMonsterLibrary=new Button("Import Monster Library");
      importMonsterLibrary.setOnAction(new importMonsterLibrary());
      gridpaneFilters.add(importMonsterLibrary,0,14);

      
      Button updateDisplay=new Button("Update Display");
      updateDisplay.setOnAction(new updateDisplay());
      gridpaneFilters.add(updateDisplay,0,0);
      
      //buttons in the Full monster scroll pane
      AddToEncounterBL=new ArrayList();
      ShowStatblockBL=new ArrayList();


      
      
      //lists for monster names in scroll pane 1 and enconter pane
      monsterNamesLst=new ArrayList();
      monstersInEncounterLst=new ArrayList();
      encounterNamesUnqiue=new ArrayList();
      DisplayscrollPane = new ScrollPane();
      DisplayscrollPane.setPrefSize(1140, 300);
      
      DisplayscrollPane2 = new ScrollPane();
      DisplayscrollPane2.setPrefSize(400, 300);
      monsterInEncounterScrollPane=new ScrollPane();
      monsterInEncounterScrollPane.setPrefSize(300, 200);
  
      
      gridpane.add(DisplayscrollPane,0,2);
      gridpane.add(new Label(""),0,4);
      //gridpane.add(DisplayscrollPane2,0,4);
      
      
      //setting up the display underneath the monsterlist
      playersnEncounters.add(new Label("Encounter Monsters"),0,0);
      playersnEncounters.add(monsterInEncounterScrollPane,0,1);
      playersnEncounters.add(new Label("Encounter Players"),2,0);
      
      if(playerAmount==null)
      {playerAmount=new TextField("1");}
      if(playerLevel==null)
      {playerLevel=new TextField("1");}
      
      playerLevel.textProperty().addListener((observable, oldValue, newValue) -> {calculateDifficulty();});
      playerAmount.textProperty().addListener((observable, oldValue, newValue) -> {calculateDifficulty();});
      GridPane gp=new GridPane();
      
      gp.add(new Label("Player Amount  "),0,0);
      gp.add(playerAmount,0,1);
      gp.add(new Label("Player Level  "),1,0);
      gp.add(playerLevel,1,1);
      
      playersnEncounters.add(gp,2,1);
      EncounterDifficulty=new Label("Encounter Diffuclty");
      playersnEncounters.add(EncounterDifficulty,3,1);
      
      

      
      gridpane.add(playersnEncounters,0,5);
      
      
      try
      {

      connection = DriverManager.getConnection(url, user, password);
      Statement stmt = connection.createStatement();
      stmt.execute("USE DndEncounters");
      
      //need to get all source books in database for this one
      // Execute a query to get the source books
       ResultSet rsBooks = stmt.executeQuery("SELECT Distinct sourcebook FROM monster");
   
       // Add the source books to the ComboBox
       SourcebookFilter.getItems().clear();
       SourcebookFilter.getItems().add("None");
       SourcebookFilter.setValue("None");
       while (rsBooks.next()) {
           SourcebookFilter.getItems().add(rsBooks.getString("sourcebook"));
       }
      
      
      ResultSet rs = stmt.executeQuery("Select MonsterName, CreatureType, Size, ChallengeRating, Alignment, Sourcebook from Monster");
      DisplayMonsters(rs);

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
      
      //HANDLE ENCOUNTER LIST//
      //monstersInEncounterListView = new ListView<>();
      //monstersInEncounterListView.getItems().add("Test");
     // monstersInEncounterGridpane.add(monstersInEncounterListView, 0, 0);
      
      root = new HBox(gridpane,gridpaneFilters);
      Scene scene = new Scene(root, screenWidth, screenHeight);
      pStage.setScene(scene);
      pStage.show();
      if(!encounterNameLoad.equals(""))
      {
      loadDataFromEncounter(encounterNameLoad);
      }

      //code for making enter search work
      
      scene.setOnKeyPressed(ev ->
         {
            if (ev.getCode() == KeyCode.ENTER) 
            { 
               ActionEvent ae = new ActionEvent();
               System.out.println("workled");
               displayUpdater.handle(ae);
               //DisplayMonsters(monstersInEncounterLst);
               ev.consume();
            }
         });

      


   }
   private boolean isParseable(String str)
   {
      try 
      {
      Integer.parseInt(str);
      return true;
      } 
      catch (NumberFormatException e) 
      {return false;}
   }
   private void calculateDifficulty()
   {
      int totalXP=0;
      if(isParseable(playerAmount.getText())&&Integer.parseInt(playerAmount.getText())<=0)
      {
         EncounterDifficulty.setText("Include a player");
      }
      else
      {
         if(isParseable(playerAmount.getText())&&isParseable(playerLevel.getText())&&(Integer.parseInt(playerAmount.getText())>0))
         {
            try
            {
               Statement stmt = connection.createStatement();
               int level = Integer.parseInt(playerLevel.getText())-1;   
               if(level<0||level>19)
               {System.out.print("error With Level");EncounterDifficulty.setText("Player Level must be between 1-20");}
               int totalAmountOfMonsters=0;
               for(int i=0;i<encounterNamesUnqiue.size();i++)
               {
                  //sql querry encounterNamesUnqiue
                  
                  String query="";

                  //loops through the list of unique names to get the total experience from each
                  //monster then multiply that by the amount of that unique monsters
                  ResultSet rsBooks = stmt.executeQuery("SELECT Experience FROM Monster where monsterName='"+encounterNamesUnqiue.get(i)+"'");
                  while(rsBooks.next())
                  {
                     //System.out.println("amount of monsters"+amountofMonsters.get(i)+"\n xp"+rsBooks.getInt("Experience"));
                     totalXP += amountofMonsters.get(i)*rsBooks.getInt("Experience");
                     totalAmountOfMonsters+=amountofMonsters.get(i);   
                  }
               }
               //totalXP=totalXP/playerAmountTemp;
         
               
               
               
               boolean temp=true;
               String difficultyLevel="";
               int playerAmountTemp=Integer.parseInt(playerAmount.getText());
               int adjXP=calculateADJXP(totalXP,totalAmountOfMonsters,playerAmountTemp);
               //magic number here
               for(int a=0;a<4&&temp;a++)
               {
                  if(a==0&&(adjXP/playerAmountTemp)<encounterDifficulty[level][a])
                  {difficultyLevel="Trivial";temp=false;}
                  if(a==1&&(adjXP/playerAmountTemp)<encounterDifficulty[level][a])
                  {difficultyLevel="Easy";temp=false;}
                  if(a==2&&(adjXP/playerAmountTemp)<encounterDifficulty[level][a])
                  {difficultyLevel="Medium";temp=false;}
                  if(a==3&&(adjXP/playerAmountTemp)<encounterDifficulty[level][a])
                  {difficultyLevel="Hard";temp=false;}
                  else if(a==3&&(adjXP/playerAmountTemp)<encounterDifficulty[level][a]*2)
                  {difficultyLevel="Deadly";temp=false;}
                  else if(a==3){difficultyLevel="Deadly++";temp=false;}
               } 
               
               EncounterDifficulty.setText("Total xp= "+totalXP+"\nAdjusted XP= "+adjXP+"\nDifficulty: "+difficultyLevel); 

            }
            catch(Exception e)
            {System.out.println("error occured in calculate difficulty");e.printStackTrace();}
         }
         else
         {
         EncounterDifficulty.setText("Include a Monster");
         }
         
         
      }
   }
   private int calculateADJXP(int totalXP,int totalAmountOfMonsters,int playerAmount)
   {
      int adjXP=0;
      if(playerAmount<3)
      {
         if(totalAmountOfMonsters==1)
         {adjXP=(int)(totalXP*1.5);}
         else if(totalAmountOfMonsters==2)
         {adjXP=totalXP*2;}
         else if(totalAmountOfMonsters<=6)
         {adjXP=(int)(totalXP*2.5);}
         else if(totalAmountOfMonsters<=10)
         {adjXP=totalXP*3;}
         else if(totalAmountOfMonsters<=14)
         {adjXP=totalXP*4;}
         else
         {adjXP=totalXP*5;}
      }
      else if(playerAmount<6)
      {
         if(totalAmountOfMonsters==1)
         {adjXP=totalXP;}
         else if(totalAmountOfMonsters==2)
         {adjXP=(int)(totalXP*1.5);}
         else if(totalAmountOfMonsters<=6)
         {adjXP=totalXP*2;}
         else if(totalAmountOfMonsters<=10)
         {adjXP=(int)(totalXP*2.5);}
         else if(totalAmountOfMonsters<=14)
         {adjXP=totalXP*3;}
         else
         {adjXP=totalXP*4;}
      }
      else
      {
         if(totalAmountOfMonsters==1)
         {adjXP=totalXP/2;}
         else if(totalAmountOfMonsters==2)
         {adjXP=totalXP;}
         else if(totalAmountOfMonsters<=6)
         {adjXP=(int)(totalXP*1.5);}
         else if(totalAmountOfMonsters<=10)
         {adjXP=totalXP*2;}
         else if(totalAmountOfMonsters<=14)
         {adjXP=(int)(totalXP*2.5);}
         else
         {adjXP=totalXP*3;}
      }
   return adjXP;
   }
   
   private void DisplayMonstersInEncounter(ArrayList<String> lst)
   {
         addMonsterBL=new ArrayList();
         subtractMonsterBL=new ArrayList();
         showStatblockEncounterBL=new ArrayList();
         //vbox.getChildren().remove(monstersInEncounterGridpane);
         monstersInEncounterGridpane=new GridPane();
         monstersInEncounterGridpane.setStyle("-fx-background-color: white;");
         //header label
         Label headLbl=new Label("Monsters");
         headLbl.setPadding(new Insets(10, 10, 10, 10));
         headLbl.setFont(Font.font("monospaced", 11));
         monstersInEncounterGridpane.add(headLbl,0,0);
         
         //Space
         Label spaceLbl=new Label("");
         addMonsterDisplay.add(spaceLbl,0,1);
         ArrayList<String> uniqueNames=new ArrayList();
         ArrayList<Integer> Total=new ArrayList();

         //makes a set of unique names from the list passed in
         for(int i=0;i<lst.size();i++)
         {
            if(!uniqueNames.contains(lst.get(i)))
            {
               uniqueNames.add(lst.get(i));
               Total.add(1);
            }
            else
            {
            Total.set(uniqueNames.indexOf(lst.get(i)), Total.get(uniqueNames.indexOf(lst.get(i))) + 1);
            }
         
         }

         //display 
         for(int i=0;i<uniqueNames.size();i++)
         {
               spaceLbl=new Label("");
               monstersInEncounterGridpane.add(spaceLbl,0,i*2+1);
               //System.out.println(result);
               //Monster information Label
               Label lbl=new Label(uniqueNames.get(i).substring(0,uniqueNames.get(i).indexOf("...")));
               Label amount=new Label(" "+Total.get(i)+" ");
               lbl.setPadding(new Insets(10, 10, 10, 10));
               lbl.setFont(Font.font("monospaced", 11));
               monstersInEncounterGridpane.add(lbl,0,i*2+2);
               
               //Buttons
               
               //add monster to encounter
               Button addToEncounter =new Button("+");
               monstersInEncounterGridpane.add(addToEncounter,1,i*2+2);
               
               addToEncounter.setOnAction(new addToEncounter());
               monstersInEncounterGridpane.add(amount,2,i*2+2);
               addMonsterBL.add(addToEncounter);
               
               
               //subtract monster from encouter
               Button subtractFromEncounter =new Button("-");
               monstersInEncounterGridpane.add(subtractFromEncounter,3,i*2+2);
               
               
               subtractFromEncounter.setOnAction(new subtractFromEncounter());
               subtractMonsterBL.add(subtractFromEncounter);
               
               //show stablocks in encounter
               Button showEncounter=new Button("show Statblock");
               monstersInEncounterGridpane.add(showEncounter,4,i*2+2);
               
               
               showEncounter.setOnAction(new showStatblockInEncounter());
               showStatblockEncounterBL.add(showEncounter);
               
         }     
         encounterNamesUnqiue=uniqueNames;
         //sets the amount of monsters list to the Total list which counts the amount
         //of names in the names list and totals them by unique names
         amountofMonsters=Total;
         
         //monsterInEncounterScrollPane.add(monstersInEncounterGridpane);
         //addMonsterDisplay.setMaxSize(1100,800);
         monsterInEncounterScrollPane.setContent(monstersInEncounterGridpane);
         calculateDifficulty();
      
   }
   
   
   //ToDo add a scrollbar to the side of this add buttons to show statblock and add to encounter
   /** This function displays the monsters in a list */
   private void DisplayMonsters(ResultSet rs)
   {
      try
      {
         AddToEncounterBL=new ArrayList();
         ShowStatblockBL=new ArrayList();
         monsterNamesLst=new ArrayList();
         gridpane.getChildren().remove(addMonsterDisplay);
         addMonsterDisplay=new GridPane();
         addMonsterDisplay.setStyle("-fx-background-color: white;");
         ResultSetMetaData rsmd = rs.getMetaData();
         int columnsNumber = rsmd.getColumnCount();
         String header="";
         
         String strTotal="";
         //Adds The Header label and a space afterwards
         for (int i = 1; i <= columnsNumber; i++) 
         {
         
            
            header=rsmd.getColumnName(i);
            if(i==1)
            {
               while(header.length()-30<0)
               {
                  header+=" ";
               }
            }
            if (i==4)
            {
               header="CR        ";
            }
            else
            {
               while(header.length()-20<0)
               {
                  header+=" ";
                  
               }
            }
            strTotal+=header;
         }
         
         
         //header label
         Label headLbl=new Label(strTotal);
         headLbl.setPadding(new Insets(10, 10, 10, 10));
         headLbl.setFont(Font.font("monospaced", 11));
         addMonsterDisplay.add(headLbl,0,0);
         
         //Space
         Label spaceLbl=new Label("");
         addMonsterDisplay.add(spaceLbl,0,1);
         
         
         //used to place the Labels in the correct height of the gridpane
         int rsCount=0;
         // Iterate over the ResultSet, moving to the next row with each iteration
         while (rs.next())
            {
               // Iterate over the columns in the row
               //adds labes for each monster in the querry
               String result="";
               monsterNamesLst.add(rs.getString(1)+"..."+rs.getString(6));

               //source books System.out.println("rsc5"+rs.getString(6));

               for (int i = 1; i <= columnsNumber; i++) 
               {
                  String columnValue = rs.getString(i);
                  if(i==1)
                  {
                     while(columnValue.length()<30)
                     {
                        columnValue+=" ";
                     }
                  }
                  if (i==4)
                  {
                     while(columnValue.length()<10)
                     {
                     columnValue+=" ";
                     }
                  }
                  else
                  {
                     while(columnValue.length()<20)
                     {
                     columnValue+=" ";
                     }
                  }
                  result+=columnValue;
                  
                  
                  
                  
               }
               
               //Spacer Label
               Label spaceLbl2=new Label("");
               addMonsterDisplay.add(spaceLbl2,0,rsCount*2+1);
               //System.out.println(result);
               //Monster information Label
               Label lbl=new Label(result);
               lbl.setPadding(new Insets(10, 10, 10, 10));
               lbl.setFont(Font.font("monospaced", 11));
               addMonsterDisplay.add(lbl,0,rsCount*2+2);
               
               //Buttons
               Button addToEncounter =new Button("Add to Encounter");
               addMonsterDisplay.add(addToEncounter,1,rsCount*2+2);
               AddToEncounterBL.add(addToEncounter);
               //sets it so the display is update
               //EventHandler<ActionEvent> universalEncounterAdditionButtonHandler = new addToList();
               addToEncounter.setOnAction(new addToList());
               
               
               Button monsterStatblockButton =new Button("Show Statblock");
               monsterStatblockButton.setOnAction(new showStatblock());
               addMonsterDisplay.add(monsterStatblockButton,2,rsCount*2+2);
               ShowStatblockBL.add(monsterStatblockButton);
               rsCount++;
               
            
         }
         gridpane.add(addMonsterDisplay,0,2);
         addMonsterDisplay.setMaxSize(1100,800);
         DisplayscrollPane.setContent(addMonsterDisplay);
       }
       catch(Exception e)
       {System.out.println("Print Failed");e.printStackTrace();}
       
   }
   private void PrintList(ArrayList lst)
   {
      for(int i=0;i<lst.size();i++)
      {
      System.out.println("Name: "+lst.get(i));
      }
   }
   
   /**Adds a monster to the list*/
   private class addToList implements EventHandler<ActionEvent> 
   {
        /**
         * handle Override the abstract method handle()
         */
         //encounterNamesUnqiue
         
        ArrayList<String> monstersInEncounter = new ArrayList<>();

      public void handle(ActionEvent event) 
      {
         Object source = event.getSource();
         for(int i=0;i<AddToEncounterBL.size();i++)
         {
            if(source==AddToEncounterBL.get(i))
            {
            //write code to open statblock in another window
               monstersInEncounterLst.add(monsterNamesLst.get(i));
               //addToEncounterList(monstersInEncounter, monsterNamesLst.get(i));
               //System.out.println(monsterNamesLst.get(i) + " added to encounter.");
               DisplayMonstersInEncounter(monstersInEncounterLst);
            
            }
         }
      }
   }
   
   
   
   
   
   
   /**Opens up a monster statblock*/
   private class showStatblock implements EventHandler<ActionEvent> 
   {
        /**
         * handle Override the abstract method handle()
         */
         
      public void handle(ActionEvent event) 
      {
         
         Object source = event.getSource();
         for(int i=0;i<ShowStatblockBL.size();i++)
         {
            if(source==ShowStatblockBL.get(i))
            {
            //write code to add monster to encounter
               String mName=monsterNamesLst.get(i).substring(0,monsterNamesLst.get(i).indexOf("..."));
               String mStatblock=monsterNamesLst.get(i).substring(monsterNamesLst.get(i).indexOf("...")+3);
               DNDShowStatblock newStatblock = new DNDShowStatblock(url,user,password,mName,mStatblock);
               Stage stage2 = new Stage();
               newStatblock.start(stage2);
            }
         }
      }
   }
   /**Opens up a monster statblock*/
   private class showStatblockInEncounter implements EventHandler<ActionEvent> 
   {
      public void handle(ActionEvent event)
      {
         
         Object source = event.getSource();
         for(int i=0;i<showStatblockEncounterBL.size();i++)
         {
            
            if(source==showStatblockEncounterBL.get(i))
            {
               String mName=encounterNamesUnqiue.get(i).substring(0,encounterNamesUnqiue.get(i).indexOf("..."));
               String mStatblock=encounterNamesUnqiue.get(i).substring(encounterNamesUnqiue.get(i).indexOf("...")+3);
               DNDShowStatblock newStatblock = new DNDShowStatblock(url,user,password,mName,mStatblock);
               Stage stage2 = new Stage();
            newStatblock.start(stage2);
            }
         }
      }
   }
   
   private class createMonster implements EventHandler<ActionEvent> 
   {
        /**
         * handle Override the abstract method handle()
         */
         
      public void handle(ActionEvent event) 
      {
         DNDMonsterCreatorSimple newMonster = new DNDMonsterCreatorSimple(url,user,password);
         Stage stage2 = new Stage();
         newMonster.start(stage2);
      }
   }
   private class createMonsterADV implements EventHandler<ActionEvent> 
   {
        /**
         * handle Override the abstract method handle()
         */
         
      public void handle(ActionEvent event) 
      {
         DNDMonsterCreator newMonster = new DNDMonsterCreator(url,user,password);
         Stage stage2 = new Stage();
         newMonster.start(stage2);
      }
   }
   private class updateDisplay implements EventHandler<ActionEvent> 
   {
        /**
         * handle Override the abstract method handle()
         */
         
      public void handle(ActionEvent event) 
      {
         try
         {
         searchQuery=searchBar.getText();
         if(searchQuery.equals("Filter Monster Names here"))
         {searchQuery="";}
         ResultSet rs;
         Statement stmt = connection.createStatement();
         String query="";
         
         ResultSet rsBooks = stmt.executeQuery("SELECT Distinct sourcebook FROM monster");
         String temp=SourcebookFilter.getValue();
          // Add the source books to the ComboBox
         SourcebookFilter.getItems().clear();
         SourcebookFilter.getItems().add("None");
         
         while (rsBooks.next()) {
         SourcebookFilter.getItems().add(rsBooks.getString("sourcebook"));
         }
         SourcebookFilter.setValue(temp);
         //code for filtering by creature type
         if(typeFilters.getValue().equals("None"))
         {
         query = "Select MonsterName, CreatureType, Size, ChallengeRating, Alignment, Sourcebook from Monster where MonsterName LIKE '%"+searchQuery+"%' ";
         }
         else if(typeFilters.getValue().equals("Other"))
         {
         query="SELECT MonsterName, CreatureType, Size, ChallengeRating, Alignment, Sourcebook FROM Monster WHERE MonsterName LIKE '%" + searchQuery + "%' AND CreatureType NOT IN ('Aberration', 'Beast', 'Celestial', 'Construct', 'Dragon', 'Elemental', 'Fey', 'Fiend', 'Giant', 'Humanoid', 'Monstrosity', 'Ooze', 'Plant', 'Undead')";
         }
         else
         {
         query= "Select MonsterName, CreatureType, Size, ChallengeRating, Alignment, Sourcebook from Monster where MonsterName LIKE '%"+searchQuery+"%' AND CreatureType='"+typeFilters.getValue()+"' ";
         }
         
         //if user selected other exclude all other sizes else make sure that they did not select none
         //if they did not then add their filter to the query
         if(sizeFilters.getValue().equals("Other"))
         {
         query +=" AND Size NOT IN ('Tiny', 'Small', 'Medium', 'Large', 'Huge', 'Gargantuan')";
         }
         else if(!sizeFilters.getValue().equals("None"))
         {
         query +=" AND Size ='"+sizeFilters.getValue()+"'";
         }
         
         
          //Code for alignment same as above but for alignment
         if(alignmentFilter.getValue().equals("Other"))
         {
         query +=" AND Alignment NOT IN ('Lawful Good', 'Neutral Good', 'Chaotic Good', 'Lawful Neutral', 'True Neutral', 'Chaotic Neutral', 'Lawful Evil', 'Neutral Evil', 'Chaotic Evil','Unaligned')";
         }
         else if(!alignmentFilter.getValue().equals("None"))
         {
         query +=" AND Alignment='"+alignmentFilter.getValue()+"'";
         }
         
         query+=" AND ChallengeRating >="+crLow.getText()+" AND ChallengeRating <=" +crHigh.getText();
         System.out.println(query);
        
         if(!SourcebookFilter.getValue().equals("None"))
         {
         query +=" AND Sourcebook='"+SourcebookFilter.getValue()+"'";
         }
         if(!statFilters.getValue().equals("None"))
         {
         query +=" ORDER BY "+statFilters.getValue()+" DESC";
         }

         rs = stmt.executeQuery(query);
         DisplayMonsters(rs);

         root = new HBox(gridpane,gridpaneFilters);
         Scene scene = new Scene(root, screenWidth, screenHeight);
         pStage.setScene(scene);
         pStage.show();
         scene.setOnKeyPressed(ev ->
         {
            if (ev.getCode() == KeyCode.ENTER) 
            { 
               ActionEvent ae = new ActionEvent();
               System.out.println("workled");
               displayUpdater.handle(ae);
               //DisplayMonsters(monstersInEncounterLst);
               ev.consume();
            }
         });
         }
         catch(Exception e)
         {System.out.println("Update Failed"+e);}
      }
   }
   private class addToEncounter implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent event)
      {
         
         Object source = event.getSource();
         for(int i=0;i<addMonsterBL.size();i++)
         {
            
            if(source==addMonsterBL.get(i))
            { 
            monstersInEncounterLst.add(encounterNamesUnqiue.get(i));
            DisplayMonstersInEncounter(monstersInEncounterLst);
            }
         }
      }
      
   }
   private class subtractFromEncounter implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent event)
      {
         
         Object source = event.getSource();
         for(int i=0;i<subtractMonsterBL.size();i++)
         {
            
            if(source==subtractMonsterBL.get(i))
            { 
            monstersInEncounterLst.remove(encounterNamesUnqiue.get(i));
            DisplayMonstersInEncounter(monstersInEncounterLst);
            }
         }
      }
      
   }
   
   private void overwriteHandeling()
   {
      //code to open a confirm to overwrite tab
      try
      {
      Statement stmt = connection.createStatement();
      String query="";
      final String[] confirm=new String[1];
      final CountDownLatch latch = new CountDownLatch(1);
      Platform.runLater(()->{
      DNDOverwriteEncounter DEB=new DNDOverwriteEncounter(url,user,password,driver,encounterName.getText());
      Stage stage2 = new Stage();
      DEB.start(stage2);    
      DEB.dataProperty().addListener((obs, oldData, newData) -> 
         {
         //System.out.println("Data received: " + newData);
         confirm[0]=newData;
         latch.countDown();
         });
      });
      latch.await();
      if(!confirm[0].equals("yes"))
      {return;}
      else
      {
      //need to delete encounter
      Statement rmv = connection.createStatement();
      rmv.executeUpdate("DELETE FROM Encounter WHERE EncounterName = '" + encounterName.getText() + "'");
      Path p =Paths.get(encounterName.getText()+".txt");
      Files.delete(p);
      }

      // Insert into ENCOUNTER table
      String sql = "INSERT INTO ENCOUNTER (EncounterName, playerLevel, playerAmount) VALUES (?, ?, ?)";
      String sqlPop="INSERT INTO ENCOUNTER (EncounterName, playerLevel, playerAmount) VALUES ('" + encounterName.getText() + "', '" + playerLevel.getText() + "', '" + playerAmount.getText() + "')";
      PreparedStatement pstmt = connection.prepareStatement(sql);
      pstmt.setString(1, encounterName.getText());
      pstmt.setString(2, playerLevel.getText());
      pstmt.setString(3, playerAmount.getText());
      pstmt.executeUpdate();
      
      
      try {
      // Open given file in append mode.
      BufferedWriter out = new BufferedWriter(new FileWriter(encounterName.getText()+".txt", true));
      out.write(sqlPop+";");
      out.write("\n\n");
      out.close();
      }
      catch (IOException e) {
      System.out.println("exception occurred" + e);
      }

      // Insert into APPEARS table

      for(int i=0;i<encounterNamesUnqiue.size();i++)
      {

         //we need to change how we select the sourcebook see create encounter for how
         String sourcebook="";
         ResultSet rs =stmt.executeQuery("SELECT SourceBook FROM MONSTER WHERE MonsterName = '" + encounterNamesUnqiue.get(i) + "'");
         if (rs.next()) 
         {
         sourcebook = rs.getString("SourceBook");
         }
         sql = "INSERT INTO APPEARS (MonsterName, SourceBook, EncounterName, Amount) VALUES (?, ?, ?, ?)";
         sqlPop = "INSERT INTO APPEARS (MonsterName, SourceBook, EncounterName, Amount) VALUES ('" + encounterNamesUnqiue.get(i) + "', '" + sourcebook + "', '" + encounterName.getText() + "', " + amountofMonsters.get(i) + ")";
         pstmt = connection.prepareStatement(sql);
         pstmt.setString(1, encounterNamesUnqiue.get(i));
         pstmt.setString(2, sourcebook);
         pstmt.setString(3, encounterName.getText());
         //the line below is confusing me does it just set the amount of monsters to the total amount???
         pstmt.setInt(4, amountofMonsters.get(i));
         pstmt.executeUpdate();
         try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(new FileWriter(encounterName.getText()+".txt", true));
            out.write(sqlPop+";");
            out.write("\n\n");
            out.close();
         }
         catch (IOException e) {
            System.out.println("exception occurred" + e);
         }
      }
      Platform.runLater(()->{
      pStage.close();});
      }
      catch (Exception e) {
      System.out.println("exception occurred" + e);
      }
   }

   private class createEncounter implements EventHandler<ActionEvent> 
   {
        /**
         * handle Override the abstract method handle()
         */
         
      public void handle(ActionEvent event) 
      {
         //dont need to check if monsters are in database the only way to access them is through the database
         //need to add several items to the database but need to verify they work beforehand
         try
         {

            Statement stmt = connection.createStatement();
            String query="";
            connection.setAutoCommit(false);
            
            //code to check if the encounter already exists
            PreparedStatement pstmtq = connection.prepareStatement("SELECT EncounterName from ENCOUNTER WHERE EncounterName=?");
            pstmtq.setString(1, encounterName.getText());
            ResultSet encounterExists=pstmtq.executeQuery();
            if(encounterExists.next())
            {
               //code to open a confirm to overwrite tab //need to run in new thread to not stop javafx thread
               Thread thread=new Thread(()->{
               overwriteHandeling();});
               thread.start();
               return;

            }
            
            
            //querys we need to make
            // Insert into ENCOUNTER table
            String sql = "INSERT INTO ENCOUNTER (EncounterName, playerLevel, playerAmount) VALUES (?, ?, ?)";
            String sqlPop="INSERT INTO ENCOUNTER (EncounterName, playerLevel, playerAmount) VALUES ('" + encounterName.getText() + "', '" + playerLevel.getText() + "', '" + playerAmount.getText() + "')";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, encounterName.getText());
            pstmt.setString(2, playerLevel.getText());
            pstmt.setString(3, playerAmount.getText());
            pstmt.executeUpdate();
            try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(new FileWriter(encounterName.getText()+".txt", true));
            out.write(sqlPop+";");
            out.write("\n\n");
            out.close();
            }
            catch (IOException e) {
            System.out.println("exception occurred" + e);
            }
            
            
            
            // Insert into APPEARS table
            
            
            // thise loops throught the list of unique encounter names and adds them to the encounter with the amount
            //of times that they appear in the
            for(int i=0;i<encounterNamesUnqiue.size();i++)
            {
               //we need to change how we select sourcebook
               sql = "INSERT INTO APPEARS (MonsterName, SourceBook, EncounterName, Amount) VALUES (?, ?, ?, ?)";
               String name=encounterNamesUnqiue.get(i).substring(0,encounterNamesUnqiue.get(i).indexOf("..."));
               String sourceBook=encounterNamesUnqiue.get(i).substring(encounterNamesUnqiue.get(i).indexOf("...")+3);
               sqlPop = "INSERT INTO APPEARS (MonsterName, SourceBook, EncounterName, Amount) VALUES ('" + name + "', '" + sourceBook + "', '" + encounterName.getText() + "', " + amountofMonsters.get(i) + ")";
               pstmt = connection.prepareStatement(sql);
               pstmt.setString(1, name );
               pstmt.setString(2,sourceBook );
               pstmt.setString(3, encounterName.getText());
               pstmt.setInt(4, amountofMonsters.get(i));
               pstmt.executeUpdate();
               try {
               // Open given file in append mode.
               BufferedWriter out = new BufferedWriter(new FileWriter(encounterName.getText()+".txt", true));
               out.write(sqlPop+";");
               out.write("\n\n");
               out.close();
               }
               catch (IOException e) {
               System.out.println("exception occurred" + e);
               }
            }
            pStage.close();
         }
         catch(Exception e)
         {
            System.out.println("Error in createEncounter\n");e.printStackTrace();
            try 
            {
               connection.rollback();  // Rollback changes on error
               System.out.println("Transaction rolled back successfully.");
            } 
            catch (SQLException e1) 
            {
               System.out.println("Couldn't rollback transaction.");
               e1.printStackTrace();
            }
         }
         finally 
         {
            try 
            {
               connection.setAutoCommit(true);  // Turn auto-commit back on
            } 
            catch (SQLException e2) 
            {
               System.out.println("Couldn't turn auto-commit back on.");
               e2.printStackTrace();
            }
         }
      }
   }
   private void loadDataFromEncounter(String encounterNameToLoad)
   {
   editingEncounter=true;
   monstersInEncounterLst=new ArrayList();
   
   try
   {
      //connection = DriverManager.getConnection(url, user, password);
      Statement stmt = connection.createStatement();
      Statement stmt1 = connection.createStatement();
      Statement stmt2 = connection.createStatement();
      //stmt.execute("USE DndEncounters");

      //we need to change how we find the sourcebook
      ResultSet rs = stmt.executeQuery("Select MonsterName,amount,SourceBook  from appears where EncounterName='"+encounterNameToLoad+"'");
      while(rs.next())
      {
         String monsterName = rs.getString("MonsterName");
         String sourceBook = rs.getString("SourceBook");
         int amount = rs.getInt("amount");
         for(int i=0;i<amount;i++)
         {
         monstersInEncounterLst.add(monsterName+"..."+sourceBook);
         }
      }
      ResultSet rs1 = stmt.executeQuery("Select playerLevel,playerAmount  from encounter where EncounterName='"+encounterNameToLoad+"'");
      while(rs1.next())
      {
         playerAmount.setText(rs1.getString("playerAmount"));
         playerLevel.setText(rs1.getString("playerLevel"));
      }
      calculateDifficulty();
      DisplayMonstersInEncounter(monstersInEncounterLst);
      }
      catch(Exception e)
      {System.out.println("Error in loadDataFromEncounter");e.printStackTrace();}
      encounterName.setText(encounterNameToLoad);
   }
   private class importMonsterLibrary implements EventHandler<ActionEvent>
   {
      /**
       * handle Override the abstract method handle()
       */

      public void handle(ActionEvent event)
      {
         DownloadMonsterLibrary DEB=new DownloadMonsterLibrary(url,user,password);
         Stage stage2 = new Stage();
         DEB.start(stage2);
      }
   }
   
}