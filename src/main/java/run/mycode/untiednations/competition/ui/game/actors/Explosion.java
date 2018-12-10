package run.mycode.untiednations.competition.ui.game.actors;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

/**
 *
 * @author bdahl
 */
public class Explosion extends AnimatedActor {
    private static final List<Image> FRAMES = loadFrames();
    
    public Explosion(Point2D pos) {
        super(pos, FRAMES, 66, false);
    }
    
    private static List<Image> loadFrames() {
        List<Image> frms = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            frms.add(new Image(Explosion.class.getResourceAsStream(
                    "/images/explosion" + i + ".png")));
        }
        
        return frms;
    }
}
