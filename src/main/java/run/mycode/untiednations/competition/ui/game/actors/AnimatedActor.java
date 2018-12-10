package run.mycode.untiednations.competition.ui.game.actors;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

/**
 *
 * @author bdahl
 */
public abstract class AnimatedActor extends Actor {
    private List<Image> frames;
    private long frameTime;
    private long elapsedTime;
    private long totalTime;
    private Runnable callback;
    private boolean loop;
    
    public AnimatedActor() {
        super();
        frames = new ArrayList<>();
        this.elapsedTime = 0;
    }
    
    public AnimatedActor(Point2D pos) {
        this();
        
        setPosition(pos);
    }
    
    public AnimatedActor(Point2D pos, List<Image> frames, long frameTimeMillis, boolean loopAnimation) {
        this(pos);
        this.frames.addAll(frames);
        this.frameTime = frameTimeMillis;
        this.totalTime = frameTimeMillis * this.frames.size();
        this.loop = loopAnimation;
    }
    
    public void setAnimation(List<Image> frames, long frameTimeMillis, boolean loopAnimation) {
        this.frames = new ArrayList<>(frames);
        this.frameTime = frameTimeMillis;
        this.totalTime = frameTimeMillis * this.frames.size();
        this.loop = loopAnimation;
    }
    
    public void onFinish(Runnable callback) {
        this.callback = callback;
    }
    
    
    @Override
    public void update(long deltaTimeMillis) {
        elapsedTime += deltaTimeMillis;
        
        if (elapsedTime >= totalTime) {
            if (callback != null) {
                callback.run();
            }
            
            elapsedTime = loop ? elapsedTime % totalTime : totalTime;
        }
        
        int frameNum = (int)(elapsedTime / frameTime) % frames.size();
        this.setImage(frames.get(frameNum));
    }
}
