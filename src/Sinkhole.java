import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Sinkhole class
 */
public class Sinkhole {
    private final Image SINKHOLE = new Image("res/sinkhole.png");
    private final static int DAMAGE_POINTS = 30;
    private final Point position;
    private boolean isActive;

    /**
     * Instantisates a new Sinkhole
     * @param startX: Starting x position read from CSV file
     * @param startY: Starting y position read from CSV file
     */
    public Sinkhole(int startX, int startY){
        this.position = new Point(startX, startY);
        this.isActive = true;
    }

    /**
     * Method that performs state update
     */
    public void update() {
        if (isActive){
            SINKHOLE.drawFromTopLeft(this.position.x, this.position.y);
        }
    }

    /**
     * Gets Sinkhole bounding box
     * @return Rectangle
     */
    public Rectangle getBoundingBox(){
        return new Rectangle(position, SINKHOLE.getWidth(), SINKHOLE.getHeight());
    }

    /**
     * Gets sinkhole damage points
     * @return int
     */
    public int getDamagePoints(){
        return DAMAGE_POINTS;
    }

    /**
     * Gets if sinkhole is active or not.
     * It will be inactive if Fae has stepped on it.
     * @return boolean
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets the active status of sinkhole
     * @param active: boolean
     */
    public void setActive(boolean active) {
        isActive = active;
    }
}