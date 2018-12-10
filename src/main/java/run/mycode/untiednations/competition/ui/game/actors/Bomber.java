package run.mycode.untiednations.competition.ui.game.actors;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author dahlem.brian
 */
public class Bomber extends LinearMover {

    private static final Image IMG = new Image(Bomber.class.getResourceAsStream("/images/bomber.png"));
    private static final Media SOUND = new Media(Explosion.class.getResource("/sounds/airplane.mp3").toExternalForm());
    private static final MediaPlayer PLAYER = new MediaPlayer(SOUND);
    private static int playcount = 0;

    public Bomber() {
        super(IMG);
    }

    public Bomber(Point2D start) {
        super(IMG, start);
    }

    @Override
    public void playSound() {
        if (playcount == 0) {
            PLAYER.setVolume(0);

            Timeline fade = new Timeline(
                    new KeyFrame(Duration.seconds(1),
                            new KeyValue(PLAYER.volumeProperty(), 1)));
            PLAYER.setCycleCount(AudioClip.INDEFINITE);
            PLAYER.play();
            fade.play();
        }
        playcount++;
    }

    @Override
    public void stopSound(boolean immediate) {
        if (immediate) {
            PLAYER.stop();
            playcount = 0;
        } else {
            if (playcount > 0) {
                playcount--;
            }

            if (playcount <= 0) {
                playcount = 0;
                Timeline fade = new Timeline(
                        new KeyFrame(Duration.seconds(1),
                                new KeyValue(PLAYER.volumeProperty(), 0)));

                fade.setOnFinished(ae -> {
                    PLAYER.stop();
                });

                fade.play();
            }
        }

    }
}
