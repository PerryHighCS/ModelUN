package run.mycode.untiednations.competition.ui.game.actors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author bdahl
 */
public class Explosion extends AnimatedActor {
    private static final List<Image> FRAMES = loadFrames();
    private static final Media sound = new Media(Explosion.class.getResource("/sounds/Explosion.mp3").toExternalForm());
    
    private MediaPlayer mediaPlayer;
    
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
    
    @Override
    public void playSound() {
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
    
    @Override
    public void stopSound(boolean immediate) {
        mediaPlayer.stop();
    }
}
