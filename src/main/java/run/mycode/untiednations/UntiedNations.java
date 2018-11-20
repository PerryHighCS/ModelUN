package run.mycode.untiednations;

import run.mycode.untiednations.competition.StartupController;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import run.mycode.untiednations.countries.example.GandiLand;
import run.mycode.untiednations.countries.example.GenghisStan;
import run.mycode.untiednations.countries.example.Providence;
import run.mycode.untiednations.countries.example.TitForTat;
import run.mycode.untiednations.countries.Delegate;


public class UntiedNations extends Application {

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
    
    
    private List<Delegate> getDemoMembers() {
        List<Delegate> members = new ArrayList<>();
        
        members.add(new GandiLand());
        members.add(new GenghisStan());
        members.add(new Providence());
        members.add(new TitForTat());
        
        return members;
    }

}
