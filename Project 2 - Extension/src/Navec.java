import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.lang.Math;

/**
 * Navec class that extends from abstract class Enemy.
 */
public class Navec extends Enemy {

    private final static String NAVEC_LEFT = "res/navec/navecLeft.png";
    private final static String NAVEC_RIGHT = "res/navec/navecRight.png";
    private final static String NAVEC_LEFT_INVIN = "res/navec/navecInvincibleLeft.PNG";
    private final static String NAVEC_RIGHT_INVIN = "res/navec/navecInvincibleRight.PNG";
    private final static String NAVEC_FIRE = "res/navec/navecFire.png";
    private static DrawOptions ROTATION = new DrawOptions();
    private Image navecFire;
    private final static int MAX_HEALTH_POINTS = 80;
    private final static int DAMAGE_POINTS_NAVEC = 20;

    private boolean navecAttack;
    private int frameCountInvin = 0;
    private boolean navecInvin;
    private Point centrePoint;
    private int timeScale;
    private double originalSpeed;

    /**
     * Instantiates a new Navec
     * @param startX: Starting x position of Navec read from CSV file. Int.
     * @param startY: Starting y position of Navec, read from CSV file. Int.
     */
    public Navec(int startX, int startY){
        this.position = new Point(startX, startY);
        healthPoints = MAX_HEALTH_POINTS;
        facingRight = getRandomBoolean();
        speed = getRandomSpeed();
        direction = getRandomNumber();
        isAlive = true;
        this.originalSpeed = speed;
        this.navecAttack = false;
        this.navecInvin = false;
        this.timeScale = 0;


        COLOUR.setBlendColour(GREEN);
        if(direction == RIGHT || facingRight){
            currentImage = new Image(NAVEC_RIGHT);
            this.facingRight = true;
        } else if (direction == LEFT || !facingRight){
            currentImage = new Image(NAVEC_LEFT);
            this.facingRight = false;
        }
    }

    /**
     * Performs Navec state updates.
     * @param level: Level1 only, as Navec is only in level1.
     * @param input: Input from keyboard.
     */
    @Override
    public void update(Level1 level, Input input){

        if (isAlive){
            setPrevPosition();
            if (this.direction == LEFT){
                if(facingRight){
                    this.currentImage = new Image(NAVEC_LEFT);
                    this.facingRight = false;
                }
                move(-this.speed, 0);
            } else if (this.direction == RIGHT){
                if (!facingRight){
                    this.currentImage = new Image(NAVEC_RIGHT);
                    this.facingRight = true;
                }
                move(this.speed, 0);
            } else if (this.direction == UP){
                move(0, -this.speed);
            } else if (this.direction == DOWN){
                move(0, this.speed);
            }
        }

        /* If Navec is invincible, a different Navec image is rendered for a certain amount of time.*/
        if(this.navecInvin){
            if(facingRight){
                this.currentImage = new Image(NAVEC_RIGHT_INVIN);
            } else if(!facingRight){
                this.currentImage = new Image(NAVEC_LEFT_INVIN);
            }
            frameCountInvin++;
        }
        /* Render normal Navec image once he is not invincible anymore*/
        if(frameCountInvin > INVIN_FRAME_COUNT){
            this.navecInvin = false;
            frameCountInvin = 0;
            if(facingRight){
                this.currentImage = new Image(NAVEC_RIGHT);
            } else if(!facingRight){
                this.currentImage = new Image(NAVEC_LEFT);
            }
        }

        if(healthPoints <= 0){
            isAlive = false;
        }
        if(isAlive){
            this.currentImage.drawFromTopLeft(position.x, position.y);
            level.checkCollisionsNavec(this);
            renderHealthPoints();
            level.checkOutOfBoundsNavec(this);

        }

        /* If input L was pressed, speed of Navec increases.*/
        if (input.wasPressed(Keys.L)){
            if(this.timeScale <= 2) {
                this.timeScale++;
                if(this.timeScale == 0){
                    this.speed = this.originalSpeed;
                    System.out.println("Sped up, Speed: " + this.timeScale);
                } else if (this.timeScale > 0 && this.timeScale <= 3){
                    this.speed = this.speed + this.speed/2.0;
                    System.out.println("Sped up, Speed: " + this.timeScale);
                } else if (this.timeScale < 0 && this.timeScale >= -3){
                    this.speed = this.speed + this.speed/2.0;
                    System.out.println("Sped up, Speed: " + this.timeScale);
                }
            }
        }
        /* If input K was pressed, speed of Navec decreases*/
        if(input.wasPressed(Keys.K)){
            if(this.timeScale >= -2) {
                this.timeScale--;
                if(this.timeScale == 0){
                    this.speed = this.originalSpeed;
                    System.out.println("Slowed down, Speed: " + this.timeScale);
                } else if (this.timeScale < 0 && this.timeScale >= -3){
                    this.speed = this.speed - this.speed/2.0;
                    System.out.println("Slowed down, Speed: " + this.timeScale);
                } else if (this.timeScale > 0 && this.timeScale <= 3){
                    this.speed = this.speed - this.speed/2.0;
                    System.out.println("Slowed down, Speed: " + this.timeScale);
                }
            }

        }

    }

    /**
     * Method that makes Navec move in the opposite direction if he collides with a static object or the boundary
     */
    public void moveBack(){
        this.position = prevPosition;
        if(this.direction == RIGHT){
            this.currentImage = new Image(NAVEC_LEFT);
            this.direction = LEFT;
            this.facingRight = false;
        } else if (this.direction == LEFT){
            this.currentImage = new Image(NAVEC_RIGHT);
            this.direction = RIGHT;
            this.facingRight = true;
        } else if (this.direction == UP){
            this.direction = DOWN;
        } else if (this.direction == DOWN){
            this.direction = UP;
        }
    }

    /**
     * Method that renders Navec's percentage health bar
     */
    @Override
    public void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/MAX_HEALTH_POINTS) * 100;
        if(percentageHP > ORANGE_BOUNDARY){
            COLOUR.setBlendColour(GREEN);
        }
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", position.x, position.y-HEALTHBAR_OFFSET_Y, COLOUR);
    }

    /**
     * Renders Navec's fire image.
     * @param playerCentre: Coordinates needed to calculate which fire image to render. Point.
     * @param player: Check if fire collides with Fae. Player.
     */
    @Override
    public void renderFire(Point playerCentre, Player player){
        navecFire = new Image(NAVEC_FIRE);
        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),
                player.getCurrentImage().getHeight());
        double fireCentreX;
        double fireCentreY;

        /* Renders fire from top-left*/
        if((playerCentre.x <= this.centrePoint.x) && (playerCentre.y <= this.centrePoint.y)){
            fireCentreX = centrePoint.x - currentImage.getWidth();
            fireCentreY = centrePoint.y - currentImage.getHeight();
            navecFire.draw(fireCentreX, fireCentreY);
            Rectangle fireBox = new Rectangle(fireCentreX-navecFire.getWidth()/2.0,
                    fireCentreY-navecFire.getHeight()/2.0, navecFire.getWidth(), navecFire.getHeight());
            checkFireCollision(faeBox, fireBox, player);

        }
        /* Renders fire from bottom-left*/
        else if ((playerCentre.x <= this.centrePoint.x) && (playerCentre.y > this.centrePoint.y)){
            fireCentreX = centrePoint.x-currentImage.getWidth();
            fireCentreY = centrePoint.y+currentImage.getHeight();
            ROTATION.setRotation(-Math.PI/2.0);
            navecFire.draw(fireCentreX, fireCentreY, ROTATION);
            Rectangle fireBox = new Rectangle(fireCentreX-navecFire.getWidth()/2.0,
                    fireCentreY-navecFire.getHeight()/2.0, navecFire.getWidth(), navecFire.getHeight());
            checkFireCollision(faeBox, fireBox, player);


        }
        /* Renders fire from top-right */
        else if ((playerCentre.x > this.centrePoint.x) && (playerCentre.y <= this.centrePoint.y)){
            fireCentreX = centrePoint.x+currentImage.getWidth();
            fireCentreY = centrePoint.y-currentImage.getHeight();
            ROTATION.setRotation(Math.PI/2.0);
            navecFire.draw(fireCentreX, fireCentreY, ROTATION);
            Rectangle fireBox = new Rectangle(fireCentreX-navecFire.getWidth()/2.0,
                    fireCentreY-navecFire.getHeight()/2.0, navecFire.getWidth(), navecFire.getHeight());
            checkFireCollision(faeBox, fireBox, player);


        }
        /* Renders fire from bottom-right*/
        else if ((playerCentre.x > this.centrePoint.x) && (playerCentre.y > this.centrePoint.y)){
            fireCentreX = centrePoint.x+currentImage.getWidth();
            fireCentreY = centrePoint.y+currentImage.getHeight();
            ROTATION.setRotation(Math.PI);
            navecFire.draw(fireCentreX, fireCentreY, ROTATION);
            Rectangle fireBox = new Rectangle(fireCentreX-navecFire.getWidth()/2.0,
                    fireCentreY-navecFire.getHeight()/2.0, navecFire.getWidth(), navecFire.getHeight());
            checkFireCollision(faeBox, fireBox, player);

        }

    }


    /**
     * Checks if Fae collides with fire, if she does, she takes damage.
     * @param faeBox: Used to check if Fae intersects fire. Uses Rectangle class in Bagel.
     * @param fireBox: Fire's intersection range. Uses Rectangle class in Bagel.
     * @param player: Fae takes damage if she intersects with fire. Player.
     */
    private void checkFireCollision(Rectangle faeBox, Rectangle fireBox, Player player){
        if(faeBox.intersects(fireBox)){
            if (!player.getInvincible()){
                player.setHealthPoints(Math.max(player.getHealthPoints() - DAMAGE_POINTS_NAVEC,0));
                System.out.println("Navec inflicts " + DAMAGE_POINTS_NAVEC + " damage points on Fae. " +
                        "Fae's current health: " + player.getHealthPoints() + "/" + player.getMaxHealthPoints());
                player.setInvincible(true);
            }
        }

    }

    /**
     * Gets position of Navec
     * @return Point
     */
    public Point getPosition(){
        return position;
    }

    /**
     * Gets the current navec image
     * @return Image
     */
    public Image getCurrentImage(){
        return currentImage;
    }

    /**
     * Gets Navec's bounding box
     * @return Rectangle
     */
    public Rectangle getBoundingBox() {
        return new Rectangle(position, currentImage.getWidth(), currentImage.getHeight());
    }

    /**
     * Gets Navec's centre point
     * @return Point
     */
    public Point getCentrePoint(){
        double newX = position.x + (currentImage.getWidth() / 2.0);
        double newY = position.y + (currentImage.getHeight() / 2.0);
        centrePoint = new Point(newX, newY);
        return centrePoint;
    }

    /**
     * Sets if Navec is in attack mode or not.
     * @param bool: boolean
     */
    public void setNavecAttack(boolean bool){
        this.navecAttack = bool;
    }

    /**
     * Gets if Navec is in attack mode or not
     * @return boolean
     */
    public boolean getNavecAttack(){
        return this.navecAttack;
    }

    /**
     * Gets Navec's current health
     * @return int
     */
    public int getHealthPoints() {
        return healthPoints;
    }

    /**
     * Sets Navec's current health
     * @param healthPoints: int
     */
    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    /**
     * Sets if Navec is Invincible or not
     * @param bool: boolean
     */
    public void setInvin(boolean bool){
        this.navecInvin = bool;
    }

    /**
     * Gets if Navec is invincible or not
     * @return: boolean
     */
    public boolean getInvin(){
        return this.navecInvin;
    }

    /**
     * If Navec is alive, will return true, if dead, will return false.
     * @return boolean
     */
    public boolean isAlive(){
        return isAlive;
    }

    /**
     * Gets the maximum health of Navec
     * @return int
     */
    public int getMaxHealthPoints(){
        return MAX_HEALTH_POINTS;
    }


}

