import bagel.Font;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.*;

import java.util.Random;

/**
 * Abstract enemy class used by Demon and Navec
 */
public abstract class Enemy {

    protected final static int ORANGE_BOUNDARY = 65;
    protected final static int RED_BOUNDARY = 35;
    protected final static int FONT_SIZE = 15;
    protected final Font FONT = new Font("res/frostbite.ttf", FONT_SIZE);
    protected final static DrawOptions COLOUR = new DrawOptions();
    protected final static Colour GREEN = new Colour(0, 0.8, 0.2);
    protected final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    protected final static Colour RED = new Colour(1, 0, 0);


    protected int direction;
    protected final static int LEFT = 0;
    protected final static int RIGHT = 1;
    protected final static int UP = 2;
    protected final static int DOWN = 3;


    protected final static double MOVE_SIZE_LOW = 0.2;
    protected final static double MOVE_SIZE_HIGH = 0.7;
    protected final static int INVIN_FRAME_COUNT = 180;
    protected final static int HEALTHBAR_OFFSET_Y = 6;
    protected double speed;
    protected boolean isAlive;
    protected Image currentImage;
    protected Point prevPosition;

    protected Point position;

    protected int healthPoints;

    protected boolean facingRight;

    /**
     * Method that performs state update for every enemy.
     * @param level: Level1
     * @param input: Input
     */
    public abstract void update(Level1 level, Input input);

    /**
     * Method that randomly generates a boolean
     * @return: True or false. Boolean.
     */
    public static boolean getRandomBoolean(){
        int upperBound = 2;
        Random rand = new Random();
        int int_random = rand.nextInt(upperBound);

        if (int_random == 1){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method that generates a random number 0-3 for direction
     * @return: 0-3 int.
     */
    public static int getRandomNumber(){
        int upperBound = 4;
        Random rand = new Random();
        int int_random = rand.nextInt(upperBound);
        return int_random;
    }

    /**
     * Method that generates a random speed between 0.2-0.7
     * @return: 0.2-0.7 double.
     */
    public static double getRandomSpeed(){
        double start = MOVE_SIZE_LOW;
        double end = MOVE_SIZE_HIGH;
        double random = new Random().nextDouble();
        double result = start + (random * (end - start));
        return result;
    }

    /**
     * Method that renders Healthbar for Navec and demon.
     */
    public void renderHealthPoints(){;}

    /**
     * Method that renders fire for demon or Navec.
     * @param playerCentre: Fae's centre coordinates. Point.
     * @param player: Fae, Player.
     */
    public void renderFire(Point playerCentre, Player player){;}

    /**
     * Sets previous position of Enemy
     */
    public void setPrevPosition(){
        this.prevPosition = new Point(position.x, position.y);
    }

    /**
     * Method that moves the enemy based on their direction.
     * @param xMove
     * @param yMove
     */
    public void move(double xMove, double yMove){
        double newX = position.x + xMove;
        double newY = position.y + yMove;
        this.position = new Point(newX, newY);
    }



}
