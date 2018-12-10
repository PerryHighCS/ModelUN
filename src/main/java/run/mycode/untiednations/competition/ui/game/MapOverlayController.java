package run.mycode.untiednations.competition.ui.game;

import java.util.ArrayList;
import java.util.List;
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
    private final List<Actor> toRemove;
    private final List<Actor> toAdd;
    private boolean clearAfter;

    private final Timeline updateTimer;
    private long lastTime;
    private boolean updating;

    public MapOverlayController(Canvas overlay) {
        this.canvas = overlay;
        this.actors = new ArrayList<>();
        this.toRemove = new ArrayList<>();
        this.toAdd = new ArrayList<>();

        this.lastTime = System.currentTimeMillis();

        updateTimer = new Timeline(new KeyFrame(
                Duration.millis(33),
                ae -> updateActors()));
        updateTimer.setCycleCount(Timeline.INDEFINITE);
        updateTimer.play();
    }

    public synchronized void addActor(Actor a) {
        if (!actors.contains(a)) {
            if (updating) {
                toAdd.add(a);
            }
            else {
                actors.add(a);
            }
        }
    }

    public synchronized void removeActor(Actor a) {
        // If the actors are currently being updated
        if (updating) {
            // flag this actor for removal
            toRemove.add(a);
        }
        else {
            // Otherwise remove it right away
            actors.remove(a);
        }
    }

    public synchronized void clearOverlay() {
        if (updating) {
            clearAfter = true;
        }
        else {
            actors.forEach(a -> a.stopSound(true));
            actors.clear();
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }

    private synchronized void updateActors() {
        long now = System.currentTimeMillis();
        long delta = now - lastTime;

        // Update all of the actors in the overlay
        updating = true;
        actors.forEach(a -> a.update(delta));     
        
        // If any actors were flagged for removal, remove them
        toRemove.forEach(a -> actors.remove(a));
        toRemove.clear();
        
        // If any actors were flagged for addition, add them
        toAdd.forEach(a -> actors.add(a));
        toAdd.clear();
        
        // If the overlay should be cleared, do so
        if (clearAfter) {
            actors.forEach(a -> a.stopSound(true));
            actors.clear();
            clearAfter = false;
        }
        updating = false;
        
        // Draw all remaining actors
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        actors.forEach(a -> a.draw(gc));

        // Update the clock time to the time this update was started
        lastTime = now;
    }
    
    private static class Item<T> {
        public Item(T item) {
            this.item = item;
            this.remove = false;
        }
        
        public T item;
        public boolean remove;
    }
}
