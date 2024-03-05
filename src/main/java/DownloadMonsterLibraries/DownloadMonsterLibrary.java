package DownloadMonsterLibraries;

import DNDSQLLauncher.DNDSQLApplication;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DownloadMonsterLibrary extends Application {
    private String url="";
    private String user="";
    private String password="";

    private TextField monsterPath;
    private Label outputLabel;
    public DownloadMonsterLibrary(String inUrl,String inUser,String inPassword)
    {
        url=inUrl;
        user=inUser;
        password=inPassword;
    }
    public void start(Stage primaryStage) {
        int width = 300;
        int height = 300;
        outputLabel=new Label("");
        monsterPath=new TextField("Enter path to Sourcebook");
        Button ParseData=new Button("Connect");
        ParseData.setOnAction(new DownloadMonsterLibrary.parse());
        HBox layer1=new HBox(monsterPath,ParseData);
        VBox root =new VBox(layer1,outputLabel);
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();



    }
    private class parse implements EventHandler<ActionEvent>
    {
        public void handle(ActionEvent event)
        {

            String pathToFile=monsterPath.getText();
            String sqlCommand="";
            String temp="";
            try{
                Connection connection = DriverManager.getConnection(url, user, password);
                BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
                Statement stmt = connection.createStatement();
                stmt.execute("USE DndEncounters");
                while(temp!=null)
                {
                    temp=reader.readLine();
                    if(temp!=null)
                    {
                        sqlCommand+=temp;
                        if(temp.contains(";"))
                        {
                            //push sql command into sql
                            stmt.execute(sqlCommand);
                            sqlCommand="";
                        }
                    }

                }
                outputLabel.setText("Monsters Successfully imported");
            }catch (FileNotFoundException e)
            {
                System.out.print("File error Detected");
                System.out.println(e);
                outputLabel.setText("File Error Detected");
            }catch (SQLException e)
            {
                System.out.print("SQLerror Detected");
                System.out.println(e);
                outputLabel.setText("SQL Error Detected");
            }
            catch (Exception e)
            {
                System.out.print("error Detected");
                System.out.println(e);
                outputLabel.setText("Error Detected");
            }

        }


    }

}

