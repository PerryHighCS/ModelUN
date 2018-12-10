package run.mycode.untiednations.competition.ui.game.actors;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author dahlem.brian
 */
public class Bomber extends LinearMover {
    private static Image img = new Image(Bomber.class.getResourceAsStream("/images/bomber.png"));
    private static final Media sound = new Media(Explosion.class.getResource("/sounds/airplane.mp3").toExternalForm());
    
    private MediaPlayer mediaPlayer;
        
    public Bomber() {
        super(img);
    }
    
    public Bomber(Point2D start) {
        super(img, start);
    }
    
    @Override
    public void playSound() {
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.volumeProperty().setValue(0);
        
        Timeline fade = new Timeline(
            new KeyFrame(Duration.seconds(1),
            new KeyValue(mediaPlayer.volumeProperty(), 1)));

        mediaPlayer.cycleCountProperty().setValue(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
        fade.play();
    }
    
    @Override
    public void stopSound(boolean immediate) {
        if (immediate) {
            mediaPlayer.stop();
        }
        else {
            Timeline fade = new Timeline(
                new KeyFrame(Duration.seconds(1),
                new KeyValue(mediaPlayer.volumeProperty(), 0)));
            fade.setOnFinished(ae -> mediaPlayer.stop());
            fade.play();
        }
        
    }
}
