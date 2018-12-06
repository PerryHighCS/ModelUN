package run.mycode.untiednations.competition.ui;

import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import run.mycode.untiednations.competition.model.GameEvent;
import run.mycode.untiednations.delegates.Delegate;
import run.mycode.untiednations.competition.model.Competition;
import run.mycode.untiednations.map.PoliticalMap;

public class GameController {
    private static final Font TYPE_FONT = Font.loadFont(GameController.class.getResource("/TELETYPE1945-1985.ttf").toExternalForm(), 18);
    private static final Font PRINT_FONT = Font.loadFont(GameController.class.getResource("/D3Electronism.TTF").toExternalForm(), 18);
    private static final Font MODERN_FONT = Font.loadFont(GameController.class.getResource("/SourceCodePro-Regular.ttf").toExternalForm(), 18);
    
    private int year;
    private final int NUM_YEARS = 100;
    
    private List<Delegate> delegates;
    
    private PoliticalMap map;
    
    
    @FXML
    ListView<GameEvent> paperTape;
    
    @FXML
    ListView<Delegate> mapLegend;
    
    public GameController() {
        year = 0;
    }
    
    @FXML
    private Label yearLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private ImageView mapView;
    
    @FXML
    private TitledPane legendPane;
    
    @FXML
    private TitledPane eventPane;
    
    @FXML
    private Accordion accordion;
    
    @FXML
    public void initialize() {
        paperTape.setCellFactory((ListView<GameEvent> list) -> new GameController.EventCell());
        mapLegend.setCellFactory((ListView<Delegate> list) -> new GameController.DelegateCell());
    }
    
    @FXML
    public void goBack(ActionEvent event) {
        if (year > 0) {
            year--;
        }
        
        yearLabel.setText("- " + (year + 1945) + " -");
        
        if (year == 0) {
            accordion.setExpandedPane(legendPane);
        }
        else {
            accordion.setExpandedPane(eventPane);
        }
    }
    
    @FXML
    public void goForward(ActionEvent event) {
        if (year < NUM_YEARS) {
            year++;
        }
        yearLabel.setText("- " + (year + 1945) + " -");
        
        if (year == 0) {
            accordion.setExpandedPane(legendPane);
        }
        else {
            accordion.setExpandedPane(eventPane);
        }
    }
    
    public void setDelegates(List<Delegate> delegates) {
        this.delegates = delegates;
       
        List<String> countryNames = delegates.stream().map(d -> d.getCountryName()).collect(Collectors.toList());
        
        map = PoliticalMap.createMap(countryNames, (int)mapView.getBoundsInLocal().getWidth());
        mapView.setImage(SwingFXUtils.toFXImage(map.createMap(true, null), null));
        
        mapLegend.setItems(FXCollections.observableArrayList(delegates));
    }
    
    public void runCompetition() {
        Competition comp = new Competition(delegates);
        comp.advanceCompetition(NUM_YEARS);
    }
    
    private class EventCell extends ListCell<GameEvent> {
        @Override
        protected void updateItem(GameEvent item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : item.toString());
            
            if (year < 40) {
                setFont(TYPE_FONT);                
            }
            else if (year < 65) {
                setFont(PRINT_FONT);                
            }
            else {
                setFont(MODERN_FONT);
            }
        }
    }
    
    private class DelegateCell extends ListCell<Delegate> {
        @Override
        protected void updateItem(Delegate item, boolean empty) {
            super.updateItem(item, empty);
            
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            }
            else {
                setText(item.getCountryName());
            
                double height = (this.getBoundsInLocal().getHeight() * 2) / 3;
                            
                java.awt.Color color = map.getColor(item.getCountryName());
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                Color fxColor = Color.rgb(r, g, b);
                                
                setBackground(new Background(new BackgroundFill(fxColor, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }

}
