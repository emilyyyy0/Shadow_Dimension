import bagel.*;
import bagel.util.*;
import java.lang.*;

public class Player {

    private int healthPoints;
    private final static int MAX_HEALTH = 100;
    private final static String FAE_LEFT = "res/faeLeft.png";
    private final static String FAE_RIGHT = "res/faeRight.png";
    private final static Font healthBar = new Font("res/frostbite.ttf", 30);
    private final static int HEALTH_X  = 20;
    private final static int HEALTH_Y = 25;
    private final static double MOVE = 2.0;
    private Image currentImage;
    private Point currentPosition;
    private Point prevPosition;
    private boolean facingRight;


    public Player(double x, double y) {
        healthPoints = MAX_HEALTH;
        currentPosition = new Point(x, y);
        facingRight = true;
        currentImage = new Image(FAE_RIGHT);


    }

    // Fae moves 2 pixels per frame
    // Every time we update we have to check if key is pressed, move the position of Fae and draw image.
    public void update(Input input, ShadowDimension gameObject) {
        if (input.isDown(Keys.UP)) {
            setPrevPosition();
            move(0, -MOVE);
        } else if (input.isDown(Keys.DOWN)) {
            setPrevPosition();
            move(0, MOVE);
        } else if (input.isDown(Keys.RIGHT)) {
            setCurrentImage(false);
            setPrevPosition();
            move(MOVE, 0);
        } else if (input.isDown(Keys.LEFT)) {
            setCurrentImage(true);
            setPrevPosition();
            move(-MOVE, 0);
        }

        this.currentImage.drawFromTopLeft(currentPosition.x, currentPosition.y);
        gameObject.checkOutOfBounds(this);
        gameObject.checkCollisions(this);

        this.renderHealthBar();;
        gameObject.checkReachedGate(this);
    }

    // Change the position of Fae
    private void move(double x, double y) {
        double newX = currentPosition.x + x;
        double newY = currentPosition.y + y;

        currentPosition = new Point(newX, newY);

    }

    // Get bounding box

    private void setCurrentImage(boolean faceLeft) {
        // if we are facing right, but we want to face left, we change current image to facing left
        if (facingRight && faceLeft) {
            facingRight = false;
            currentImage = new Image(FAE_LEFT);
        }

        // If we are not facing right and we want to face right
        if (!facingRight && !faceLeft) {
            facingRight = true;
            currentImage = new Image(FAE_RIGHT);
        }
    }


    // Set the previous position
    private void setPrevPosition() {
        prevPosition = currentPosition;
    }

    // Move Back
    public void moveBack() {
        currentPosition = prevPosition;
    }


    public Point getCurrentPosition() {
        return this.currentPosition;
    }

    public Image getImage() {
        return this.currentImage;
    }

    public void setHealth(int damage) {
        this.healthPoints -= damage;

        if (healthPoints <= 0) {
            healthPoints = 0;
        }
        System.out.println(String.format("Sinkhole inflicts 30 damage points on Fae. Fae's current health: %d/100", healthPoints));
    }

    public boolean isDead() {
        return this.healthPoints <= 0;
    }


    // Render health bar
    private void renderHealthBar() {
        int percent_health = (int) Math.round((this.healthPoints * 100) / MAX_HEALTH);

        if (percent_health >= 65) {
            healthBar.drawString(String.format("%d%%", percent_health), HEALTH_X,HEALTH_Y, new DrawOptions().setBlendColour(0, 0.8, 0.2) );
        } else if (percent_health < 65) {
            healthBar.drawString(String.format("%d%%", percent_health), HEALTH_X,HEALTH_Y, new DrawOptions().setBlendColour(0.9, 0.6, 0) );
        } else if (percent_health < 35) {
            if (percent_health <= 0) {
                percent_health = 0;
            }
            healthBar.drawString(String.format("%d%%", percent_health), HEALTH_X,HEALTH_Y, new DrawOptions().setBlendColour(1, 0, 0) );
        }

    }


    @Override
    public String toString() {
        return "Player";
    }

}
