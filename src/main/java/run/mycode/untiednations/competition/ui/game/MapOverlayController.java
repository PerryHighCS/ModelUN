package run.mycode.untiednations.competition.ui.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import run.mycode.untiednations.competition.ui.game.actors.Actor;

/**
 *
 * @author dahlem.brian
 */
public class MapOverlayController {
    private final Canvas canvas;
    private final List<Actor> actors;
    
    private final Timeline updateTimer;
    private long lastTime;
    
    public MapOverlayController(Canvas overlay) {
        this.canvas = overlay;
        this.actors = new CopyOnWriteArrayList<>();
        
        this.lastTime = System.currentTimeMillis();
        
        updateTimer = new Timeline(new KeyFrame(
                Duration.millis(33),
                ae -> updateActors()));
        updateTimer.setCycleCount(Timeline.INDEFINITE);
        updateTimer.play();
    }
    
    public void addActor(Actor a) {
        if (!actors.contains(a)) {
            actors.add(a);
        }
    }
    
    public void removeActor(Actor a) {
        actors.remove(a);
    }
    
    public void clearOverlay() {
        actors.clear();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());    
    }
    
    private void updateActors() {
        long now = System.currentTimeMillis();
        long delta = now - lastTime;
                
        actors.forEach(a -> a.update(delta));
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        actors.forEach(a -> a.draw(gc));
        
        lastTime = now;
    }    
}
