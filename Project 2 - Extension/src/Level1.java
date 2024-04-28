import bagel.Font;
import bagel.util.Point;
import bagel.*;
import bagel.util.Rectangle;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that performs and renders Level1 operations and objects.
 */
public class Level1 extends Level {

    private final static String WORLD_FILE_1 = "res/level1.csv";
    private final static String BACKGROUND_IMAGE_1 = "res/background1.png";
    private final static String TREE_FILE = "res/tree.png";
    private final static String INSTRUCTION_MESSAGE_1 = "PRESS SPACE TO START\nPRESS A TO ATTACK\nDEFEAT NAVEC TO WIN";

    private final static String WIN_MESSAGE = "CONGRATULATIONS!";

    private final static ArrayList<StaticObject> trees = new ArrayList<StaticObject>();

    private final static ArrayList<Sinkhole> sinkholes1 = new ArrayList<Sinkhole>();

    private final static ArrayList<Demon> demons = new ArrayList<Demon>();

    private Navec navec;
    private boolean gameWin = false;
    private boolean hasStartedLevel1 = false;


    /**
     * Instantiates a new Level 1 object.
     */
    public Level1(){
        readCSV();
        background = new Image(BACKGROUND_IMAGE_1);
        font = new Font("res/frostbite.ttf", INSTRUCTION_FONT_SIZE);
    }


    /**
     * Method used to read file and create objects
     */
    @Override
    public void readCSV(){
        try (BufferedReader reader = new BufferedReader(new FileReader(WORLD_FILE_1))){

            String line;

            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                switch (sections[0]) {
                    case "Fae":
                        player = new Player(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;

                    case "Tree":
                        StaticObject newTree = new StaticObject(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]), TREE_FILE);
                        trees.add(newTree);
                        break;

                    case "Sinkhole":
                        Sinkhole newShole = new Sinkhole(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        sinkholes1.add(newShole);
                        break;

                    case "Demon":
                        Demon newDemon = new Demon(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        demons.add(newDemon);
                        break;

                    case "Navec":
                        navec = new Navec(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;

                    case "TopLeft":
                        topLeft = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;

                    case "BottomRight":
                        bottomRight = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Method that updates the state of the game.
     * @param input: Input from the keyboard.
     */
    @Override
    public void update(Input input){
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        if (!hasStartedLevel1){
            drawMessage(INSTRUCTION_MESSAGE_1);
            if(input.wasPressed(Keys.SPACE)){
                hasStartedLevel1 = true;
            }
        }

        if(gameOver){
            drawEndScreen(END_MESSAGE);
        }

        if(hasStartedLevel1 && !gameOver &&!playerWin && !gameWin){
            background.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

            for (Sinkhole current: sinkholes1){
                current.update();
            }

            for (StaticObject current: trees){
                current.update();
            }

            for (Demon current: demons) {
                current.update(this, input);
            }

            player.update(input, this);

            navec.update(this, input);

            if(player.isDead()){
                gameOver = true;
            }

            if(!navec.isAlive()){
                gameWin = true;
            }

        }

        if(gameWin){
            drawEndScreen(WIN_MESSAGE);
        }
    }

    /**
     * Method that checks Fae's collition with objects in Level1.
     * If player collides with a demon, and is in attack mode, the demon will take damage.
     * Also checks if Fae comes into demon or Navec's range, then fire is rendered.
     * @param player: Player.
     */
    @Override
    public void checkCollisions(Player player){
        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),
                player.getCurrentImage().getHeight());

        Rectangle navecBox = navec.getBoundingBox();
        /* If Fae intersects Navec, she can attack if Navec is not invincible.*/
        if(faeBox.intersects(navecBox)){
            if (player.getAttack() && !navec.getInvin()){
                navec.setHealthPoints(Math.max(navec.getHealthPoints() - player.getFaeDamagePoints(), 0));
                navec.setInvin(true);
                System.out.println("Fae inflicts " + player.getFaeDamagePoints() + " damage points on Navec. "
                        + "Navec's current health: " + navec.getHealthPoints() + "/" + navec.getMaxHealthPoints());

            }
        }

        /* Checks if Fae has come inside Navec's range*/
        Point navecCentre = navec.getCentrePoint();
        Point faeCentre = player.getCentrePoint();
        if(navecCentre.distanceTo(faeCentre) <= 200){
            navec.setNavecAttack(true);
        } else if (navecCentre.distanceTo(faeCentre) > 200){
            navec.setNavecAttack(false);
        }

        /* Renders fire based on Fae's position if in Navec's range.*/
        if(navec.getNavecAttack()){
            navec.renderFire(faeCentre, player);
        }

        /* Checks if Fae has come into any demons attack range. If she has, render demon fire.*/
        for(Demon current: demons){
            Point demonCentre = current.getCentrePoint();
            if(current.isAlive()) {
                if (demonCentre.distanceTo(faeCentre) <= 150) {
                    current.setDemonAttack(true);
                } else if (demonCentre.distanceTo(faeCentre) > 150) {
                    current.setDemonAttack(false);
                }
                if (current.getDemonAttack()) {
                    current.renderFire(faeCentre, player);
                }
            }
        }

        /* If demon is not invincible and Fae is in attack mode, demon takes damage.*/
        for(Demon current: demons){
            Rectangle demonBox = current.getBoundingBox();
            if(faeBox.intersects(demonBox) && current.isAlive()){
                if (player.getAttack() && !current.getInvin()){
                    current.setHealthPoints(Math.max(current.getHealthPoints() - player.getFaeDamagePoints(), 0));
                    current.setInvin(true);
                    System.out.println("Fae inflicts " + player.getFaeDamagePoints() + " damage points on Demon. "
                            + "Demon's current health: " + current.getHealthPoints()
                            + "/" + current.getMaxHealthPoints());
                }
            }
        }

        /* Checks if Fae collides with trees, if she does, she moves back*/
        for (StaticObject current: trees){
            Rectangle treeBox = current.getBoundingBox();
            if(faeBox.intersects(treeBox)){
                player.moveBack();
            }
        }

        /* Checks if Fae collides with sinkholes, if she does, she takes damage and the sinkhole is deactivated.*/
        for (Sinkhole hole : sinkholes1){
            Rectangle holeBox = hole.getBoundingBox();
            if(hole.isActive() && faeBox.intersects(holeBox)){
                player.setHealthPoints(Math.max(player.getHealthPoints() - hole.getDamagePoints(), 0));
                player.moveBack();
                hole.setActive(false);
                System.out.println("Sinkhole inflicts " + hole.getDamagePoints() + " damage points on Fae. "
                        + "Fae's current health: " + player.getHealthPoints() + "/" + player.getMaxHealthPoints());
            }
        }
    }

    /**
     * Method that checks if demon collides with any static objects such as sinkholes and trees.
     * Demon moves in the opposite direction if it collides with any static objects.
     * @param demon: Demon.
     */
    public void checkCollisionsDemon(Demon demon){
        Rectangle demonBox = new Rectangle(demon.getPosition(), demon.getCurrentImage().getWidth(),
                demon.getCurrentImage().getHeight());

        for(StaticObject current: trees){
            Rectangle treeBox = current.getBoundingBox();
            if(demonBox.intersects(treeBox)){
                demon.moveBack();
            }
        }

        for (Sinkhole current: sinkholes1){
            Rectangle holeBox = current.getBoundingBox();
            if(demonBox.intersects(holeBox) && current.isActive()){
                demon.moveBack();
            }
        }
    }


    /**
     * Method that checks if demon goes out of the screen.
     * @param demon: Demon
     */
    public void checkOutOfBoundsDemon(Demon demon){
        Point currentPosition = demon.getPosition();
        if((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeft.y) || (currentPosition.x < topLeft.x)
                || (currentPosition.x > bottomRight.x)) {
            demon.moveBack();
        }
    }

    /**
     * Method that checks if Navec goes out of the screen.
     * @param navec: Navec
     */
    public void checkOutOfBoundsNavec(Navec navec) {
        Point currentPosition = navec.getPosition();
        if ((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeft.y) || (currentPosition.x < topLeft.x)
                || (currentPosition.x > bottomRight.x)) {
            navec.moveBack();
        }
    }

    /**
     * Method that checks if Navec collides with any static objects.
     * @param navec: Navec
     */
    public void checkCollisionsNavec(Navec navec){
        Rectangle navecBox = new Rectangle(navec.getPosition(), navec.getCurrentImage().getWidth(),
                navec.getCurrentImage().getHeight());

        for(StaticObject current: trees){
            Rectangle treeBox = current.getBoundingBox();
            if(navecBox.intersects(treeBox)){
                navec.moveBack();
            }
        }

        for (Sinkhole current: sinkholes1){
            Rectangle holeBox = current.getBoundingBox();
            if(navecBox.intersects(holeBox) && current.isActive()){
                navec.moveBack();
            }
        }

    }

}

