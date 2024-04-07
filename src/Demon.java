import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Demon class that extends from Enemy
 */
public class Demon extends Enemy{

    private final static String DEMON_LEFT = "res/demon/demonLeft.png";
    private final static String DEMON_RIGHT = "res/demon/demonRight.png";

    private final static String DEMON_LEFT_INVIN = "res/demon/demonInvincibleLeft.PNG";
    private final static String DEMON_RIGHT_INVIN = "res/demon/demonInvincibleRight.PNG";
    private final static int MAX_HEALTH_POINTS = 40;

    private final static int DAMAGE_POINTS_DEMON = 10;

    private final static String DEMON_FIRE = "res/demon/demonFire.png";

    private static DrawOptions ROTATION = new DrawOptions();

    private Image demonFire;

    private boolean demonAttack;

    private boolean isPassive;

    private Point centrePoint;

    private boolean demonInvin = false;

    private int frameCountInvin = 0;

    private int timeScale;

    private double originalSpeed;


    /**
     * Instantiates a new Demon
     * @param startX: Starting x position of Demon from CSV file
     * @param startY: Starting y position of Demon from CSV file
     */
    public Demon(int startX, int startY){
        this.position = new Point(startX, startY);
        healthPoints = MAX_HEALTH_POINTS;
        facingRight = getRandomBoolean();
        this.isPassive = getRandomBoolean();
        direction = getRandomNumber();
        speed = getRandomSpeed();
        COLOUR.setBlendColour(GREEN);
        this.originalSpeed = speed;
        this.demonAttack = false;
        isAlive = true;
        if (direction == RIGHT || facingRight){
            currentImage = new Image(DEMON_RIGHT);
            this.facingRight = true;
        } else if (direction == LEFT || !facingRight) {
            currentImage = new Image(DEMON_LEFT);
            this.facingRight = false;
        }
    }

    /**
     * Method that performs state update on demon.
     * @param level: Level1
     * @param input: Input
     */
    @Override
    public void update(Level1 level, Input input){
        /* If demon is aggressive, render it with movement.*/
        if(isAlive && !isPassive) {
            setPrevPosition();
            if (this.direction == LEFT){
                if(facingRight){
                    this.currentImage = new Image(DEMON_LEFT);
                    this.facingRight = false;
                }
                move(-this.speed,0);
            } else if (this.direction == RIGHT){
                if (!facingRight){
                    this.currentImage = new Image(DEMON_RIGHT);
                    this.facingRight = true;
                }
                move(this.speed, 0);

            } else if (this.direction == UP){
                move(0, -this.speed);
            } else if (this.direction == DOWN){
                move(0, this.speed);
            }

        }
        /* If demon is passive just render it*/
        else if (isAlive && isPassive){
            this.currentImage.drawFromTopLeft(position.x, position.y);
        }

        /* If demon takes damage from Fae, render invincible demon for a certain amount of time.*/
        if(this.demonInvin){
            if(facingRight){
                this.currentImage = new Image(DEMON_RIGHT_INVIN);
            } else if(!facingRight){
                this.currentImage = new Image(DEMON_LEFT_INVIN);
            }
            frameCountInvin++;
        }
        /* Render normal demon after invincibility time runs out*/
        if(frameCountInvin > INVIN_FRAME_COUNT){
            this.demonInvin = false;
            frameCountInvin = 0;
            if(facingRight){
                this.currentImage = new Image(DEMON_RIGHT);
            } else if(!facingRight){
                this.currentImage = new Image(DEMON_LEFT);
            }
        }

        /* Increase Demon speed if L is pressed.*/
        if (input.wasPressed(Keys.L)){
            if(this.timeScale <= 2) {
                this.timeScale++;
                if(this.timeScale == 0){
                    this.speed = this.originalSpeed;
                } else if (this.timeScale > 0 && this.timeScale <= 3){
                    this.speed = this.speed + this.speed/2.0;
                } else if (this.timeScale < 0 && this.timeScale >= -3){
                    this.speed = this.speed + this.speed/2.0;
                }
            }
        }
        /* Decrease Demon speed if K is pressed.*/
        if(input.wasPressed(Keys.K)){
            if(this.timeScale >= -2) {
                this.timeScale--;
                if(this.timeScale == 0){
                    this.speed = this.originalSpeed;
                } else if (this.timeScale < 0 && this.timeScale >= -3){
                    this.speed = this.speed - this.speed/2.0;
                } else if (this.timeScale > 0 && this.timeScale <= 3){
                    this.speed = this.speed - this.speed/2.0;
                }
            }

        }

        if(this.healthPoints <= 0){
            this.isAlive = false;
        }
        if (this.isAlive){
            this.currentImage.drawFromTopLeft(position.x, position.y);
            level.checkCollisionsDemon(this);
            renderHealthPoints();
            level.checkOutOfBoundsDemon(this);
        }

    }


    /**
     * Method that changes the direction to the opposite of where demon is moving.
     */
    public void moveBack(){
        this.position = prevPosition;
        if (this.direction == RIGHT){
            this.currentImage = new Image(DEMON_LEFT);
            this.direction = LEFT;
            this.facingRight = false;
        } else if (this.direction == LEFT){
            this.currentImage = new Image(DEMON_RIGHT);
            this.direction = RIGHT;
            this.facingRight = true;
        } else if (this.direction == UP){
            this.direction = DOWN;
        } else if (this.direction == DOWN){
            this.direction = UP;
        }
    }

    /**
     * Method that renders the Demon HealthBar
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
     * Method that finds the coordinate for the centre of the demon
     * @return: Point
     */
    public Point getCentrePoint(){
        double newX = position.x + (currentImage.getWidth() / 2.0);
        double newY = position.y + (currentImage.getHeight() / 2.0);
        centrePoint = new Point(newX, newY);
        return centrePoint;
    }

    /**
     * Sets if demon is in attack mode.
     * @param bool: boolean
     */
    public void setDemonAttack(boolean bool){
        this.demonAttack = bool;
    }

    /**
     * Gets if demon is in attack mode
     * @return boolean
     */
    public boolean getDemonAttack(){
        return this.demonAttack;
    }


    /**
     * Method that renders fire based on Fae's coordinates.
     * @param playerCentre: Fae's centre coordinates. Point.
     * @param player: Fae, Player.
     */
    @Override
    public void renderFire(Point playerCentre, Player player){
        demonFire = new Image(DEMON_FIRE);
        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),player.getCurrentImage().getHeight());
        double fireCentreX;
        double fireCentreY;

        /* Renders fire from top-left*/
        if((playerCentre.x <= this.centrePoint.x) && (playerCentre.y <= this.centrePoint.y)){
            fireCentreX = centrePoint.x - currentImage.getWidth();
            fireCentreY = centrePoint.y - currentImage.getHeight();
            demonFire.draw(fireCentreX, fireCentreY);
            Rectangle fireBox = new Rectangle(fireCentreX-demonFire.getWidth()/2.0, fireCentreY-demonFire.getHeight()/2.0, demonFire.getWidth(), demonFire.getHeight());
            checkFireCollision(faeBox, fireBox, player);

        }
        /* Renders fire from bottom-left*/
        else if ((playerCentre.x <= this.centrePoint.x) && (playerCentre.y > this.centrePoint.y)){
            fireCentreX = centrePoint.x-currentImage.getWidth();
            fireCentreY = centrePoint.y+currentImage.getHeight();
            ROTATION.setRotation(-Math.PI/2.0);
            demonFire.draw(fireCentreX, fireCentreY, ROTATION);
            Rectangle fireBox = new Rectangle(fireCentreX-demonFire.getWidth()/2.0, fireCentreY-demonFire.getHeight()/2.0, demonFire.getWidth(), demonFire.getHeight());
            checkFireCollision(faeBox, fireBox, player);


        }
        /* Renders fire from top-right */
        else if ((playerCentre.x > this.centrePoint.x) && (playerCentre.y <= this.centrePoint.y)){
            fireCentreX = centrePoint.x+currentImage.getWidth();
            fireCentreY = centrePoint.y-currentImage.getHeight();
            ROTATION.setRotation(Math.PI/2.0);
            demonFire.draw(fireCentreX, fireCentreY, ROTATION);
            Rectangle fireBox = new Rectangle(fireCentreX-demonFire.getWidth()/2.0, fireCentreY-demonFire.getHeight()/2.0, demonFire.getWidth(), demonFire.getHeight());
            checkFireCollision(faeBox, fireBox, player);

        }
        /* Renders fire from bottom-right*/
        else if ((playerCentre.x > this.centrePoint.x) && (playerCentre.y > this.centrePoint.y)){
            fireCentreX = centrePoint.x+currentImage.getWidth();
            fireCentreY = centrePoint.y+currentImage.getHeight();
            ROTATION.setRotation(Math.PI);
            demonFire.draw(fireCentreX, fireCentreY, ROTATION);
            Rectangle fireBox = new Rectangle(fireCentreX-demonFire.getWidth()/2.0, fireCentreY-demonFire.getHeight()/2.0, demonFire.getWidth(), demonFire.getHeight());
            checkFireCollision(faeBox, fireBox, player);
        }

    }

    /**
     * Method that checks if Player collides with fire
     * if is does, player takes damage.
     */
    private void checkFireCollision(Rectangle faeBox, Rectangle fireBox, Player player){
        if(faeBox.intersects(fireBox)){
            if (!player.getInvincible()){
                player.setHealthPoints(Math.max(player.getHealthPoints() - DAMAGE_POINTS_DEMON,0));
                System.out.println("Demon inflicts " + DAMAGE_POINTS_DEMON + " damage points on Fae. " + "Fae's current health: " + player.getHealthPoints() + "/" + player.getMaxHealthPoints());
                player.setInvincible(true);
            }
        }

    }

    /**
     * Method that checks if the demon's health is above 0 and if it is alive.
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Sets if demon is invincible.
     * @param bool: boolean
     */
    public void setInvin(boolean bool){
        this.demonInvin = bool;
    }

    /**
     * Gets if demon is invincible
     * @return: boolean
     */
    public boolean getInvin(){
        return this.demonInvin;
    }

    /**
     * Gets demon current health
     * @return: int
     */
    public int getHealthPoints() {
        return healthPoints;
    }

    /**
     * Gets demon's maximum healthpoints.
     * @return int
     */
    public int getMaxHealthPoints(){
        return MAX_HEALTH_POINTS;
    }

    /**
     * Sets demons health
     * @param healthPoints: int
     */
    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    /**
     * Gets demon bounding box
     * @return: Rectangle
     */
    public Rectangle getBoundingBox() { return new Rectangle(position, currentImage.getWidth(), currentImage.getHeight());}

    /**
     * Gets position of demon
     * @return Point
     */
    public Point getPosition(){ return position; }

    /**
     * Gets current demon image
     * @return Image
     */
    public Image getCurrentImage() {
        return currentImage;
    }


}


