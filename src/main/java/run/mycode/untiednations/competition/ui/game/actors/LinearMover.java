package run.mycode.untiednations.competition.ui.game.actors;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

/**
 *
 * @author dahlem.brian
 */
public class LinearMover extends Actor {    
    private Point2D start;
    private Point2D end;
    private Point2D delta;
    private long moveTime;
    private long timePassed;
    private boolean moving;
    private Runnable callback;
    
    public LinearMover() {
        super();
    }
    
    public LinearMover(Image img) {
        super(img);
    }
    
    public LinearMover(Image img, Point2D pos) {
        super(img);
        
        setPosition(pos);
    }
    
    public LinearMover(Image img, Point2D start, Point2D end, long moveTimeMillis) {
        this(img, start);
        
        this.start = start;
        this.end = end;
        this.delta = start.subtract(end);
        this.moveTime = moveTimeMillis;
        this.timePassed = 0;
        this.moving = false;
        
        setPosition(start);
    }
    
    public void startMoving() {
        this.moving = true;
        this.timePassed = 0;
    }
    
    public void pauseMoving() {
        this.moving = false;
    }

    public void resumeMoving() {
        this.moving = true;
    }
    
    public boolean isMoving() {
        return moving;
    }
    
    public void moveTo(Point2D dest, long timeMillis) {
        this.start = getPosition();
        this.end = dest;
        this.delta = this.start.subtract(this.end);
        this.moveTime = timeMillis;
    }
    
    @Override
    public void update(long deltaTimeMillis) {
        if (moving) {
            this.timePassed += deltaTimeMillis;
            if (this.timePassed < moveTime) {
                double dt = (double)timePassed / moveTime;
                double dx = delta.getX() * dt;
                double dy = delta.getY() * dt;
                
                setPosition(start.add(dx, dy));
            }
            else {
                setPosition(end);
                moving = false;
                
                if (callback != null) {
                    callback.run();
                }
            }
        }
    }
    
    public void onFinish(Runnable c) {
        this.callback = c;
    }
}
