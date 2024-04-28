import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * StaticObject class for trees and walls.
 */
public class StaticObject {

    private final Image staticObjectImage;
    private final Point position;

    /**
     * Instantiates a new static object based on the filename.
     * @param startX: Starting x position read from CSV file
     * @param startY: Staring y position read from CSV file
     * @param filename: Filename that renders an image.
     */
    public StaticObject(int startX, int startY, String filename){
        this.position = new Point(startX, startY);
        this.staticObjectImage = new Image(filename);
    }

    /**
     *  Method that performs state update
     */
    public void update(){
        this.staticObjectImage.drawFromTopLeft(this.position.x, this.position.y);
    }

    /**
     * Method that gets the bounding box of the static object
     * @return Rectangle
     */
    public Rectangle getBoundingBox(){
        return new Rectangle(position, staticObjectImage.getWidth(), staticObjectImage.getHeight());
    }
}
