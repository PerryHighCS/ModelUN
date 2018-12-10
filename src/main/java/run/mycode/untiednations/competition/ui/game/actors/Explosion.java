package run.mycode.untiednations.competition.ui.game.actors;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author bdahl
 */
public class Explosion extends AnimatedActor {
    private static final List<Image> FRAMES = loadFrames();
    private static final String SOUND = 
            Explosion.class.getResource("/sounds/Explosion.mp3").toString();
    
    private static final AudioClip[] PLAYER_POOL = mediaPlayerPool(3);
    private int player = -1;
    
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
    
    private static AudioClip[] mediaPlayerPool(int poolSize) {
        final AudioClip[] players = new AudioClip[poolSize];
        
        for (int i = 0; i < poolSize; i++) {
            players[i] = new AudioClip(SOUND);
        }
        return players;
    }
    
    @Override
    public synchronized void playSound() {
        for (int j = 0; j < PLAYER_POOL.length; j++) {
            if (!PLAYER_POOL[j].isPlaying()) {
                PLAYER_POOL[j].play();
                player = j;
                break;
            }
        }
    }
    
    @Override
    public synchronized void stopSound(boolean immediate) {
        if (player != -1 && PLAYER_POOL[player].isPlaying()) {
            PLAYER_POOL[player].stop();            
        }
    }
}
