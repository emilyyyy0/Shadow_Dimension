import bagel.*;
import bagel.util.*;

public class SinkHole {

    private final static Image sinkHoleImage = new Image("res/sinkhole.png");
    private final Point position;
    private final static int DAMAGE_POINTS = 30;
    private boolean isActive;



    public SinkHole(int x, int y) {
        position = new Point(x, y);
        isActive = true;
    }

    public void update() {
        sinkHoleImage.drawFromTopLeft(position.x, position.y);
    }

    public Rectangle getBoundingBox() {
        Rectangle rectangle = new Rectangle(position, sinkHoleImage.getWidth(), sinkHoleImage.getHeight());
        return rectangle;
    }

    public void setIsActive(boolean bool) {
        this.isActive = bool;
    }

    public int getDamagePoints() {
        return DAMAGE_POINTS;
    }

    public boolean getIsActive() {
        return isActive;
    }

}
