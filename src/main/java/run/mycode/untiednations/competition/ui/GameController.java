package run.mycode.untiednations.competition.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private static final double FONT_SIZE = 18;
    private static final double LEGEND_FONT_SIZE = 14;
    private static final Font TYPE_FONT = Font.loadFont(GameController.class.getResource("/NewPress.otf").toExternalForm(), FONT_SIZE);
    private static final Font PRINT_FONT = Font.loadFont(GameController.class.getResource("/D3Electronism.TTF").toExternalForm(), FONT_SIZE);
    private static final Font MODERN_FONT = Font.loadFont(GameController.class.getResource("/SourceCodePro-Regular.ttf").toExternalForm(), FONT_SIZE);
    private static final int BASE_YEAR = 1945;
    
    private int year;
    private final int NUM_YEARS = 100;
    
    private List<Delegate> delegates;
    
    private PoliticalMap map;
    
    
    @FXML
    ListView<GameEvent> paperTape;
    
    @FXML
    ListView<DelegateInfo> mapLegend;
    
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
    
    private Competition comp;
        
    @FXML
    public void initialize() {
        paperTape.setCellFactory((ListView<GameEvent> list) -> new GameController.EventCell());
        mapLegend.setCellFactory((ListView<DelegateInfo> list) -> new GameController.DelegateCell());
        
        mapLegend.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends DelegateInfo> ov, 
                        DelegateInfo oldValue, DelegateInfo newValue) -> {
                    if (newValue != null) {
                        mapView.setImage(SwingFXUtils.toFXImage(map.createMap(true,
                            newValue.name), null));
                    }
                    else {
                        mapView.setImage(SwingFXUtils.toFXImage(map.createMap(true,
                            null), null));
                    }
        });
        
        mapLegend.focusedProperty().addListener(
                (ObservableValue<? extends Boolean> ov, 
                        Boolean wasfocused, Boolean isfocused) -> {
                    if (!isfocused) {
                        mapView.setImage(SwingFXUtils.toFXImage(map.createMap(true, null), null));
                    }
                    else {
                        mapView.setImage(SwingFXUtils.toFXImage(map.createMap(true,
                            mapLegend.getSelectionModel().getSelectedItem().name), null));
                    }
        });        
    }
    
    public void setDelegates(List<Delegate> delegates) {
        this.delegates = delegates;
        this.comp = new Competition(delegates);
        this.year = 0;
       
        List<String> countryNames = delegates.stream().map(d -> d.getCountryName()).collect(Collectors.toList());
        
        map = PoliticalMap.createMap(countryNames, (int)mapView.getBoundsInLocal().getWidth());
        mapView.setImage(SwingFXUtils.toFXImage(map.createMap(true, null), null));
                
        showYear();
    }
    
    /**
     * Move to the previous year and display the events that occurred
     * @param event 
     */
    @FXML
    public void goBack(ActionEvent event) {
        if (year > 0) {
            year--;
        }
        
        showYear();
    }
    
    /**
     * Move to the next year and display the events that occurred
     * @param event 
     */
    @FXML
    public void goForward(ActionEvent event) {
        if (year < NUM_YEARS) {
            year++;
        }
        
        showYear();        
    }
    
    private void showYear() {
        yearLabel.setText("- " + (year + BASE_YEAR) + " -");
        
        if (year == 0) {
            accordion.setExpandedPane(legendPane);
        }
        else {
            accordion.setExpandedPane(eventPane);
        }
        
        comp.advanceCompetitionTo(year);
        
        showEvents(comp.getEvents(year));
        updateDelegateList(delegates, year);
    }
    
    private void updateDelegateList(List<Delegate> delegates, int year) {
        List<DelegateInfo> dil = new ArrayList<>();
        
        for (Delegate d : delegates) {
            DelegateInfo di = new DelegateInfo();
            di.name = d.getCountryName();
            di.wealth = comp.getWealth(di.name, year);
                    
            dil.add(di);
        }
        mapLegend.setItems(FXCollections.observableArrayList(dil));
    }
    
    private void showEvents(List<GameEvent> events) {
        ObservableList<GameEvent> list = paperTape.getItems();
        list.clear();
        
        // Only show items with a message
        events.stream().filter((e) -> (!e.getAction().text.isEmpty())).forEachOrdered((e) -> {
            list.add(e);
        });        
    }
    
    private class EventCell extends ListCell<GameEvent> {
        //private Text text;
                     
        @Override
        protected void updateItem(GameEvent item, boolean empty) {
            super.updateItem(item, empty);
            
            if (!empty && item != null) {
                setPrefWidth(paperTape.getWidth()-2);
                setText(item.toString().toUpperCase());
                setWrapText(true);
                        
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
            else {
                setText(null);
            }
        }
    }
    
    private class DelegateCell extends ListCell<DelegateInfo> {
        @Override
        protected void updateItem(DelegateInfo item, boolean empty) {
            super.updateItem(item, empty);
            
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            }
            else {
                setFont(Font.font(LEGEND_FONT_SIZE));
                
                String text = item.name + ":  \uD835\uDD7D";
                
                text += String.format("%,.2f", item.wealth);
                
                setText(text);
                            
                java.awt.Color color = map.getColor(item.name);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                Color fxColor = Color.rgb(r, g, b);
                                
                setBackground(new Background(new BackgroundFill(fxColor, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }
    
    private static class DelegateInfo {
        public String name;
        public double wealth;
    }
}