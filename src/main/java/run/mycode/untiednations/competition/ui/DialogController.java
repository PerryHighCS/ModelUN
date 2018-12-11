package run.mycode.untiednations.competition.ui;

import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import run.mycode.compiler.ReportItem;

/**
 *
 * @author dahlem.brian
 */
public class DialogController {
    private static final Image ICON =
            new Image(DialogController.class.getResourceAsStream("/images/alert.png"));
    
    @FXML
    DialogPane content;
        
    @FXML
    Label messageLabel;
    
    @FXML
    public void initialize() {
        //this.content.setGraphic(ICON);        
    }
    
    public void dialogInfo(String message, List<ReportItem> details) {
        
        this.messageLabel.setText(message);
        
        ListView<ReportItem> detailList = new ListView<>();
        
        VBox container = new VBox(detailList);
        Insets margin = new Insets(20, 20, 20, 20);
        VBox.setMargin(detailList, margin);
        
        detailList.setCellFactory((ListView<ReportItem> list) -> new ReportCell());
        detailList.getItems().addAll(details);
        
        content.expandableContentProperty().set(container);
        
        content.expandedProperty().addListener((ov, oldValue, newValue) -> {
            content.getScene().getWindow().sizeToScene();
            
        });
    }
    
    
    private class ReportCell extends ListCell<ReportItem> {

        @Override
        protected void updateItem(ReportItem item, boolean empty) {
            super.updateItem(item, empty);
            
            if (item == null || empty) {
                setText("");
            }
            else if(item.isCompileMessage()) {
                String message = item.isError() ? item.getFilename() + " COMPILE ERROR\n" : "";
                message += item.getMessage();
                message += "\n at line " + item.getLine() + ", position " +
                        item.getColumn() + " in " + item.getFilename();
                
                setText(message);
            }
            else {
                setText(item.getMessage());
            }
        }
    }
}
