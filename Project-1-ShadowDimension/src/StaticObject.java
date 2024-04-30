import bagel.*;
import bagel.util.*;

/***
 * This is a static object class for walls and trees
 */
public class StaticObject {
    private static Image objectImage;
    private final Point position;


    public StaticObject(String objectImage, int x, int y) {
        this.objectImage = new Image(objectImage);
        this.position = new Point(x, y);
    }

    // Update function

    public void update() {
        objectImage.drawFromTopLeft(position.x, position.y);
    }

    public Rectangle getBoundingBox() {
        Rectangle rectangle = new Rectangle(position, objectImage.getWidth(), objectImage.getHeight());
        return rectangle;
    }

}
