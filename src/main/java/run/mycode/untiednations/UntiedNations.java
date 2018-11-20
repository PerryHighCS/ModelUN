package run.mycode.untiednations;

import run.mycode.untiednations.competition.ui.StartupController;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import run.mycode.untiednations.delegates.example.GandiLand;
import run.mycode.untiednations.delegates.example.GenghisStan;
import run.mycode.untiednations.delegates.example.Providence;
import run.mycode.untiednations.delegates.example.TitForTat;
import run.mycode.untiednations.delegates.Delegate;


public class UntiedNations extends Application {

    /**
     * Display the startup screen to prepare for a new UntiedNations competition.
     * @param stage the FXML container for the competition startup screen
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/StartupScene.fxml"));

        
        Parent root = (Parent)fxmlLoader.load();
        StartupController controller = fxmlLoader.<StartupController>getController();
        controller.setMembershipRoll(getDemoMembers());
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("UntiedNations");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
        
    /**
     * Create a list of the demonstration delegates to the UN
     * 
     * @return A list containing the demo delegates
     */
    private List<Delegate> getDemoMembers() {
        List<Delegate> members = new ArrayList<>();
        
        members.add(new GandiLand());
        members.add(new GenghisStan());
        members.add(new Providence());
        members.add(new TitForTat());
        
        return members;
    }
}
