package run.mycode.untiednations.competition.ui.game.actors;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author dahlem.brian
 */
public abstract class Actor {
    private Point2D pos;
    private double rotation;
    private Image img;
    
    public Actor() {
        
    }
    
    public Actor(Image img) {
        this.img = img;
    }
    
    public final void setImage(Image img) {
        this.img = img;
    }
    
    public final void draw(GraphicsContext g) {
        if (this.img != null) {
            //System.out.println(pos.getX() + ", " + pos.getY());
            if (rotation != 0) {
                g.save();
                g.translate(pos.getX(), pos.getY());
                g.rotate(rotation);                
                g.restore();
                g.drawImage(img, -img.getWidth(), -img.getHeight());
            }
            else {                
                g.drawImage(img, pos.getX() - img.getWidth(), pos.getY() - img.getHeight());
            }
        }
    }
    
    public final void setPosition(Point2D pos) {
        this.pos = pos;
    }
    
    public final Point2D getPosition() {
        return pos;
    }
    
    public final double getX() {
        return pos.getX();
    }
    
    public final void setX(double x) {
        this.pos = new Point2D(x, pos.getY());
    }
    
    public final void setY(double y) {
        this.pos = new Point2D(pos.getX(), y);
    }
    
    public final double getY() {
        return pos.getY();
    }
    
    public final void rotate(double degrees) {
        this.rotation += degrees;
    }
    
    public final void rotateTo(double degrees) {
        this.rotation = degrees;
    }
    
    public abstract void update(long deltaTimeMillis);
}
