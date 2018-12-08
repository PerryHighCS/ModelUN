package run.mycode.untiednations.competition.ui.game.actors;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

/**
 *
 * @author dahlem.brian
 */
public class Bomber extends LinearMover {
    private static Image img = new Image(Bomber.class.getResourceAsStream("/images/bomber.png"));
        
    public Bomber() {
        super(img);
    }
    
    public Bomber(Point2D start) {
        super(img, start);
        System.out.println("*****************" + img + " " + img == null);
    }    
}
