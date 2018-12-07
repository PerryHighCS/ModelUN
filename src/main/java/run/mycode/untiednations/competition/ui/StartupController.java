package run.mycode.untiednations.competition.ui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import run.mycode.untiednations.delegates.Delegate;

public class StartupController {
    
    @FXML
    private ListView<Delegate> memberList;
    
    @FXML
    private void rollClicked(ActionEvent event) {
        System.out.println("Roll clicked.");
    }
    
    @FXML
    private void demoClicked(ActionEvent event) {
        List<Delegate> delegates = memberList.getItems();
        
        Parent root;
        try {
            FXMLLoader fxl = new FXMLLoader(getClass().getResource("/fxml/GameScene.fxml"));
            root = fxl.load();
            
            GameController gc = fxl.<GameController>getController();
            gc.setDelegates(delegates);
            
            Stage stage = new Stage();
            
            stage.setTitle("Model UntiedNations");
            stage.setScene(new Scene(root));
            stage.show();
            
            ((Node)event.getSource()).getScene().getWindow().hide();
        }
        catch (IOException e) {
            System.err.println("Could not load competition window.");
            e.printStackTrace();            
        }
    }
           
    @FXML
    public void initialize() {
        memberList.setCellFactory((ListView<Delegate> list) -> new CountryCell());
    }    
    
    public void setMembershipRoll(List<Delegate> countries) {
        memberList.setItems(FXCollections.observableArrayList(countries));
    }
    
    private class CountryCell extends ListCell<Delegate> {
        @Override
        protected void updateItem(Delegate item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" :  item.getCountryName());
        }
    }
}
