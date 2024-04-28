import bagel.Font;
import bagel.util.Point;
import bagel.*;
import bagel.util.Rectangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that performs Level0 tasks.
 */
public class Level0 extends Level{
    private boolean goToLevel1 = false;
    private final static String WORLD_FILE = "res/level0.csv";
    private final static String WALL_FILE = "res/wall.png";
    private final static int TITLE_X = 260;
    private final static int TITLE_Y = 250;
    private final static int INS_X_OFFSET = 90;
    private final static int INS_Y_OFFSET = 190;
    private final static String INSTRUCTION_MESSAGE = "PRESS SPACE TO START\nUSE ARROW KEYS TO FIND GATE";
    private final static String GAME_TITLE = "SHADOW DIMENSION";
    private final static String LEVEL0_WIN_MESSAGE = "LEVEL COMPLETE!";

    private final static ArrayList<StaticObject> walls = new ArrayList<StaticObject>();

    private final static ArrayList<Sinkhole> sinkholes = new ArrayList<Sinkhole>();

    private int frameCount = 0;

    private final static int MESSAGE_RENDER_TIME = 180;

    private boolean levelcomplete = false;


    /**
     * Instantiates a new Level 0 object.
     */
    public Level0(){
        readCSV();
        background = new Image("res/background0.png");
        font = new Font("res/frostbite.ttf", INSTRUCTION_FONT_SIZE);

    }


    /**
     * Method used to read file and create objects
     */
    @Override
    public void readCSV () {
    try (BufferedReader reader = new BufferedReader(new FileReader(WORLD_FILE))) {

        String line;

        while ((line = reader.readLine()) != null) {
            String[] sections = line.split(",");
            switch (sections[0]) {
                case "Fae":
                    player = new Player(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                    break;
                case "Wall":
                    StaticObject newWall = new StaticObject(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]), WALL_FILE);
                    walls.add(newWall);
                    break;
                case "Sinkhole":
                    Sinkhole newSinkhole = new Sinkhole(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                    sinkholes.add(newSinkhole);
                    break;
                case "TopLeft":
                    topLeft = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                    break;
                case "BottomRight":
                    bottomRight = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                    break;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        System.exit(-1);
        }
    }


    /**
     * Performs a state update.
     * Allows the game to exit when the escape key is pressed.
     * Also renders level0 objects.
     * @param input: Input from keyboard.
     */
    @Override
    public void update(Input input){
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        /* The start screen is displayed when the game is first opened. Game starts when the user presses space. */
        if (!hasStarted){
            drawStartScreen();
            if(input.wasPressed(Keys.SPACE)){
                hasStarted = true;
            }
        }

        /* If players health is below 0, the game will end and end message will be displayed.*/
        if(gameOver){
            drawEndScreen(END_MESSAGE);
        }

        /* Perform update operations for level0 */
        if (hasStarted && !gameOver && !playerWin &&!levelcomplete){
            background.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

            for(StaticObject current: walls){
                current.update();
            }
            for(Sinkhole current: sinkholes){
                current.update();
            }
            player.update(input, this);

            if(player.isDead()){
                gameOver = true;
            }

            if(player.reachedGate()) {
                levelcomplete = true;
            }
        }
        /* If the player reaches the gate, the level0 end message is displayed for 3000 milliseconds*/
        if(levelcomplete){
            drawEndScreen(LEVEL0_WIN_MESSAGE);
            frameCount++;
        }
        /* After 3000 milliseconds, level1 can be rendered.*/
        if (player.reachedGate() && frameCount > MESSAGE_RENDER_TIME){
            isLevel0Complete = true;
            goToLevel1 = true;
        }

    }

    /**
     * Render the game title and instructions at the start of the game.
     */
    private void drawStartScreen(){
        TITLE_FONT.drawString(GAME_TITLE, TITLE_X, TITLE_Y);
        font.drawString(INSTRUCTION_MESSAGE, TITLE_X + INS_X_OFFSET, TITLE_Y + INS_Y_OFFSET);
    }

    /**
     * Method that checks Fae's collision with
     * walls and sinkholes.
     */
    @Override
    public void checkCollisions(Player player){
        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),
                player.getCurrentImage().getHeight());

        /* Iterate through an arraylist of walls to check if the Fae has collied with any.
         * If Fae has, Fae is moved back. */
        for(StaticObject current: walls){
            Rectangle wallBox = current.getBoundingBox();
            if(faeBox.intersects(wallBox)){
                player.moveBack();
            }
        }

        /* Iterate through an arraylist of sinkholes to check if Fae has collided with any.
         * If Fae has, Fae takes damage and sinkhole is deactivated.*/
        for (Sinkhole hole : sinkholes){
            Rectangle holeBox = hole.getBoundingBox();
            if(hole.isActive() && faeBox.intersects(holeBox)){
                player.setHealthPoints(Math.max(player.getHealthPoints() - hole.getDamagePoints(), 0));
                player.moveBack();
                hole.setActive(false);
                System.out.println("Sinkhole inflicts " + hole.getDamagePoints() + " damage points on Fae. " +
                        "Fae's current health: " + player.getHealthPoints() + "/" + player.getMaxHealthPoints());
            }
        }
    }

    /**
     * If level 0 is complete, go to Level 1
     * @return: boolean
     */
    public boolean getGoToLevel1(){
        return goToLevel1;
    }

}
