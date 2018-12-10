package run.mycode.untiednations.competition.ui.game;

import com.hoten.delaunay.geom.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import run.mycode.untiednations.competition.model.GameEvent;
import run.mycode.untiednations.delegates.Delegate;
import run.mycode.untiednations.competition.model.Competition;
import run.mycode.untiednations.competition.ui.game.actors.Bomber;
import run.mycode.untiednations.competition.ui.game.actors.Explosion;
import run.mycode.untiednations.competition.ui.game.map.PoliticalMap;

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
    Button back;
    
    @FXML
    Button forward;
    
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
    private AnchorPane mapView;
    
    @FXML
    private Canvas mapOverlay;
    
    @FXML
    private ProgressBar progress; 
        
    private Competition comp;
    
    private Timeline headlineTimer;
    
    private MapOverlayController overlayController;
        
    @FXML
    public void initialize() {
        overlayController = new MapOverlayController(mapOverlay);
        
        paperTape.setCellFactory((ListView<GameEvent> list) -> new GameController.EventCell());
        mapLegend.setCellFactory((ListView<DelegateInfo> list) -> new GameController.DelegateCell());
        
        mapLegend.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends DelegateInfo> ov, 
                        DelegateInfo oldValue, DelegateInfo newValue) -> {
                    Image img;
                    
                    if (newValue != null) {
                        img = SwingFXUtils.toFXImage(map.createMap(true, 
                                                                   newValue.name,
                                                                   year != 0),
                                                     null);
                        
                        
                    }
                    else {
                        img = SwingFXUtils.toFXImage(map.createMap(true, null,
                                                                   year != 0),
                                                    null);
                    }
                    
                    BackgroundImage bi = new BackgroundImage(img,
                                                 BackgroundRepeat.NO_REPEAT,
                                                 BackgroundRepeat.NO_REPEAT,
                                                 BackgroundPosition.DEFAULT,
                                                 BackgroundSize.DEFAULT);
                    mapView.setBackground(new Background(bi));
        });
        
        mapLegend.focusedProperty().addListener(
                (ObservableValue<? extends Boolean> ov, 
                        Boolean wasfocused, Boolean isfocused) -> {
                    Image img;
                    
                    if (!isfocused || mapLegend.getSelectionModel().getSelectedItem() == null) {
                        img = SwingFXUtils.toFXImage(map.createMap(true, null, 
                                                                   year != 0),
                                null);
                    }
                    else {
                        img = SwingFXUtils.toFXImage(map.createMap(true,
                                                                   mapLegend
                                                                     .getSelectionModel()
                                                                     .getSelectedItem()
                                                                     .name, 
                                                                   year != 0),
                                null);
                    }
                    
                    BackgroundImage bi = new BackgroundImage(img,
                                                 BackgroundRepeat.NO_REPEAT,
                                                 BackgroundRepeat.NO_REPEAT,
                                                 BackgroundPosition.DEFAULT,
                                                 BackgroundSize.DEFAULT);
                    mapView.setBackground(new Background(bi));
        });
    }
    
    public void setDelegates(List<Delegate> delegates) {
        this.delegates = delegates;
        this.comp = new Competition(delegates, NUM_YEARS);
        this.year = 0;
        back.setDisable(true);
       
        List<String> countryNames = delegates.stream().map(d -> d.getCountryName()).collect(Collectors.toList());
                
        map = PoliticalMap.createMap(countryNames, (int)mapOverlay.getWidth());
        
        Image img = SwingFXUtils.toFXImage(map.createMap(true, null), null);
        BackgroundImage bi = new BackgroundImage(img,
                                                 BackgroundRepeat.NO_REPEAT,
                                                 BackgroundRepeat.NO_REPEAT,
                                                 BackgroundPosition.DEFAULT,
                                                 BackgroundSize.DEFAULT);
        mapView.setBackground(new Background(bi));
        
        showYear(true);
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
        
        if (year == 0) {
            back.setDisable(true);
        }
        forward.setDisable(false);
        
        showYear(true);
    }
    
    /**
     * Move to the next year and display the events that occurred
     * @param event 
     */
    @FXML
    public void goForward(ActionEvent event) {
        if (headlineTimer == null) {
            if (year <= NUM_YEARS) {
                year++;
            }
            if (year > NUM_YEARS) {
                forward.setDisable(true);
            }
            back.setDisable(false);
        
            showYear(false);
        }
        else {
            showYear(true);
        }
    }
    
    private void showYear(boolean fast) {
        overlayController.clearOverlay();
        
        if (headlineTimer != null) {
            headlineTimer.stop();
            headlineTimer = null;
        }
        
        yearLabel.setText("- " + (year + BASE_YEAR) + " -");
        
        mapLegend.getSelectionModel().clearSelection();
        Image img;
        
        if (year == 0) {
            img = SwingFXUtils.toFXImage(map.createMap(true, null, false), null);
        }
        else {
            img = SwingFXUtils.toFXImage(map.createMap(true, null), null);
        }
        
        BackgroundImage bi = new BackgroundImage(img,
                                                 BackgroundRepeat.NO_REPEAT,
                                                 BackgroundRepeat.NO_REPEAT,
                                                 BackgroundPosition.DEFAULT,
                                                 BackgroundSize.DEFAULT);
        mapView.setBackground(new Background(bi));
        
        String statusText;
        
        switch (year) {
            case 0:
            case NUM_YEARS:
                statusText = "";
                break;
            default:
                statusText = "Untied Nations simulation in process";
                break;
        }
        
        statusLabel.setText(statusText);
        
        comp.advanceCompetitionTo(year);
        
        List<GameEvent> events = comp.getEvents(year);
        
        if (fast) {
            showEvents(events);
            updateDelegateList(delegates, year);
            progress.setProgress(1.0);
        }
        else {            
            clearEvents();
            headlineTimer = new Timeline(new KeyFrame(
                    Duration.seconds(1),
                    ae -> showNextEvent(events, delegates, year)));
            headlineTimer.setCycleCount(events.size() + 1);
            headlineTimer.play();
        }
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
    
    private void clearEvents() {
        ObservableList<GameEvent> list = paperTape.getItems();
        list.clear();
    }
    
    private void showNextEvent(List<GameEvent> events, List<Delegate> delegates, int year) {
        ObservableList<GameEvent> list = paperTape.getItems();
        
        if (list.size() < events.size()) {
            GameEvent next = events.get(list.size()); 
            list.add(next);
            startAttack(next);
            paperTape.scrollTo(list.size() - 1);
            progress.setProgress(list.size() / (double)(events.size()));
        }
        else {
            updateDelegateList(delegates, year);
            progress.setProgress(1.0);
            headlineTimer.stop();
            headlineTimer = null;
        }
    }
    
    /**
     * Display an attack between countries
     * @param evt The event that shows the attack that occurred
     */
    private void startAttack(GameEvent evt) {
        GameEvent.Attack attack = evt.getAction().attackDir;
        
        Delegate i = evt.getSource();
        Delegate j = evt.getTarget();
        
        if (j != null) {
            if (attack == GameEvent.Attack.ONEWAY ||
                attack == GameEvent.Attack.BOTH) {
                bomber(i, j);
            }
            if (attack == GameEvent.Attack.BOTH) {
                bomber(j, i);
            }
        }
        else if (attack == GameEvent.Attack.SELF) {            
            for (int k = 0; k < 10; k++) {
                Point p = map.getRandomPoint(i.getCountryName());
                explode(p);
            }
            explode(i);
        }
    }
    
    private void bomber(Delegate i, Delegate j) {
        Point iloc = map.getCapitalLocation(i.getCountryName());
        Point2D start = new Point2D(iloc.x, iloc.y);
        
        Point jloc = map.getRandomPoint(j.getCountryName());
        Point2D end = new Point2D(jloc.x, jloc.y);

        Bomber b = new Bomber(start);
        b.moveTo(end);
        b.moveTime((long)(b.moveDistance() * 15));
        b.onFinish(() -> {
            explode(jloc);
            b.moveTo(start);
            b.startMoving();
            b.onFinish(() -> overlayController.removeActor(b));
        });
        overlayController.addActor(b);
        b.startMoving();
    }
    
    private void explode(Delegate d) {
        Point loc = map.getCapitalLocation(d.getCountryName());
    
        explode(loc);
    }
    
    private void explode(Point loc) {
        Point2D pos = new Point2D(loc.x, loc.y);
        
        Explosion exp = new Explosion(pos);
        overlayController.addActor(exp);
        exp.onFinish(() -> overlayController.removeActor(exp));
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
                        
                if (year < 34) {
                    setFont(TYPE_FONT);                
                }
                else if (year < 61) {
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
