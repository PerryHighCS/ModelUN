package run.mycode.untiednations.competition.ui.game.actors;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author dahlem.brian
 */
public abstract class Actor {
    private double x;
    private double y;
    private Image img;
    
    public void setImage(Image img) {
        this.img = img;
    }
    
    public void draw(GraphicsContext g) {
        
    }
}
