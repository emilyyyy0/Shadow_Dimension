import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;

/**
 * Player class that renders Fae
 */
public class Player {
    private final static String FAE_LEFT = "res/fae/faeLeft.png";
    private final static String FAE_RIGHT = "res/fae/faeRight.png";
    private final static String FAE_ATTACK_LEFT = "res/fae/faeAttackLeft.png";
    private final static String FAE_ATTACK_RIGHT = "res/fae/faeAttackRight.png";

    private final static int FAE_DAMAGE_POINTS = 20;
    private final static int MAX_HEALTH_POINTS = 100;
    private final static double MOVE_SIZE = 2;
    private final static int WIN_X = 950;
    private final static int WIN_Y = 670;

    private final static int HEALTH_X = 20;
    private final static int HEALTH_Y = 25;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 30;
    private final Font FONT = new Font("res/frostbite.ttf", FONT_SIZE);
    private final static DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    private Point position;
    private Point prevPosition;
    private int healthPoints;
    private Image currentImage;
    private boolean facingRight;

    private boolean INVINCIBLE;
    private boolean IDLE;
    private boolean ATTACK;
    private boolean COOLDOWN;

    private static final int INVIN_TIME = 180;
    private static final int ATTACK_TIME = 60;
    private static final int ACOOLDOWN_TIME = 120;

    private int frameCountInvin = 0;
    private int frameCountAttack = 0;
    private int frameCountACooldown = 0;

    private Point centrePoint;

    /**
     * Instantiates new Player, Fae.
     * @param startX: Starting x position read from CSV File
     * @param startY: Starting y position read from CSV File
     */
    public Player(int startX, int startY){
        this.position = new Point(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = new Image(FAE_RIGHT);
        this.facingRight = true;
        this.IDLE = true;
        this.INVINCIBLE = false;
        this.ATTACK = false;
        this.COOLDOWN = false;
        COLOUR.setBlendColour(GREEN);
    }

    /**
     * Method that performs state update
     */
    public void update(Input input, Level level){
        if (input.isDown(Keys.UP)){
            setPrevPosition();
            move(0, -MOVE_SIZE);
        } else if (input.isDown(Keys.DOWN)){
            setPrevPosition();
            move(0, MOVE_SIZE);
        } else if (input.isDown(Keys.LEFT)){
            setPrevPosition();
            move(-MOVE_SIZE,0);
            if (facingRight) {
                this.currentImage = new Image(FAE_LEFT);
                facingRight = !facingRight;
            }
        } else if (input.isDown(Keys.RIGHT)){
            setPrevPosition();
            move(MOVE_SIZE,0);
            if (!facingRight) {
                this.currentImage = new Image(FAE_RIGHT);
                facingRight = !facingRight;
            }
        }

        /* If Fae is invincible, she cannot take damage for a certain amount of time*/
        if(this.INVINCIBLE){
            frameCountInvin++;
        }
        if (frameCountInvin >  INVIN_TIME){
            this.INVINCIBLE = false;
            frameCountInvin = 0;
        }

        /* If attack is not on cooldown, render attack Fae image for a certain amount of time*/
        if(input.wasPressed(Keys.A) && !this.COOLDOWN){
            this.ATTACK = true;
            this.IDLE = false;
            if (facingRight){
                this.currentImage = new Image(FAE_ATTACK_RIGHT);
            } else {
                this.currentImage = new Image(FAE_ATTACK_LEFT);
            }

        }
        if (this.ATTACK){
            frameCountAttack++;
        }

        /* When attack is on cooldown or if Fae is idle, render normal Fae image.*/
        if(frameCountAttack > ATTACK_TIME){
            this.ATTACK = false;
            this.IDLE = true;
            this.COOLDOWN = true;
            if (facingRight){
                this.currentImage = new Image(FAE_RIGHT);
            } else {
                this.currentImage = new Image(FAE_LEFT);
            }
            frameCountAttack = 0;
        }

        if (this.COOLDOWN){
            frameCountACooldown++;
        }

        if(frameCountACooldown > ACOOLDOWN_TIME){
            this.COOLDOWN = false;
            frameCountACooldown = 0;
        }

        this.currentImage.drawFromTopLeft(position.x, position.y);
        level.checkCollisions(this);
        renderHealthPoints();
        level.checkOutOfBounds(this);

    }

    /**
     * Method that stores Fae's previous position
     */
    private void setPrevPosition(){
        this.prevPosition = new Point(position.x, position.y);
    }

    /**
     * Method that moves Fae back to previous position
     */
    public void moveBack(){
        this.position = prevPosition;
    }

    /**
     * Method that moves Fae given the direction
     */
    private void move(double xMove, double yMove){
        double newX = position.x + xMove;
        double newY = position.y + yMove;
        this.position = new Point(newX, newY);
    }

    /**
     * Method that renders player health points
     */
    private void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/MAX_HEALTH_POINTS) * 100;
        if(percentageHP > ORANGE_BOUNDARY){
            COLOUR.setBlendColour(GREEN);
        }
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", HEALTH_X, HEALTH_Y, COLOUR);
    }


    /**
     * Method that returns true if Fae's health is below or equal to 0.
     * @return boolean
     */
    public boolean isDead() {
        return healthPoints <= 0;
    }

    /**
     * Method that returns true if Fae has reached the gate
     * @return boolean
     */
    public boolean reachedGate(){
        return (this.position.x >= WIN_X) && (this.position.y >= WIN_Y);
    }

    /**
     * Gets Fae's current position
     * @return Point
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Get Fae's current image
     * @return Image
     */
    public Image getCurrentImage() {
        return currentImage;
    }

    /**
     * Get Fae's current health
     * @return int
     */
    public int getHealthPoints() {
        return healthPoints;
    }

    /**
     * Sets Fae's current health points
     * @param healthPoints int
     */
    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    /**
     * Sets if Fae is invincible or not
     * @param bool boolean
     */
    public void setInvincible(boolean bool) { this.INVINCIBLE = bool;}

    /**
     * Gets Fae's maximum health points.
     * @return int
     */
    public static int getMaxHealthPoints() {
        return MAX_HEALTH_POINTS;
    }

    /**
     * Get is Fae is invincible or not
     * @return: boolean
     */
    public boolean getInvincible(){
        return INVINCIBLE;
    }

    /**
     * Get if Fae is in attack mode or not
     * @return: boolean
     */
    public boolean getAttack(){
        return ATTACK;
    }

    /**
     * Method that returns Fae's centre point for use in Fire collision checks.
     * @return: Point
     */
    public Point getCentrePoint(){
        double newX = position.x + (currentImage.getWidth() / 2.0);
        double newY = position.y + (currentImage.getHeight() / 2.0);
        centrePoint = new Point(newX, newY);
        return centrePoint;
    }

    /**
     * Get Fae's damage points
     * @return int
     */
    public int getFaeDamagePoints(){
        return FAE_DAMAGE_POINTS;
    }


}