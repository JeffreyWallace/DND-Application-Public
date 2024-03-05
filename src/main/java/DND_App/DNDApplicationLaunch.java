package DND_App;
import DND_Weather_Generator.DNDWeatherGenerator;
import DND_HP_Program.DNDHPLauncher;
import javafx.application.Application;
import javafx.stage.Stage;
import DNDSQLLauncher.DNDSQLApplication;



public class DNDApplicationLaunch extends Application
{
   public void start(Stage primaryStage) {
        // Start the DNDWeatherGenerator application
        DNDWeatherGenerator dndWeatherGenerator = new DNDWeatherGenerator();
        Stage stage1 = new Stage();
        dndWeatherGenerator.start(stage1);

        // Start the DNDHPLauncher application
        DNDHPLauncher dndHpLauncher = new DNDHPLauncher();
        Stage stage2 = new Stage();
        dndHpLauncher.start(stage2);

       DNDSQLApplication dndsqlLauncher=new DNDSQLApplication();
       Stage stage3 = new Stage();
       dndsqlLauncher.start(stage3);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
