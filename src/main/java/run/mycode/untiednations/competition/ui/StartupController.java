package run.mycode.untiednations.competition.ui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import run.mycode.untiednations.competition.model.Competition;
import run.mycode.untiednations.delegates.Delegate;

public class StartupController implements Initializable {
    
    @FXML
    private ListView<Delegate> memberList;
    
    @FXML
    private void rollClicked(ActionEvent event) {
        System.out.println("Roll clicked.");
    }
    
    @FXML
    private void demoClicked(ActionEvent event) {
        List<Delegate> delegates = memberList.getItems();
        
        Competition comp = new Competition(delegates);
        comp.advanceCompetition(100);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        memberList.setCellFactory(new Callback<ListView<Delegate>, ListCell<Delegate>>() {
            @Override public ListCell<Delegate> call(ListView<Delegate> list) {
                return new CountryCell();
            }
        });
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
