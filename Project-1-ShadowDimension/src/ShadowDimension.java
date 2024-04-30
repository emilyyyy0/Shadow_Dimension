import bagel.*;
import bagel.util.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.FieldPosition;

/**
 * Skeleton Code for SWEN20003 Project 1, Semester 2, 2022
 *
 * Please enter your name below
 * @author Emily Gong
 */

public class ShadowDimension extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "SHADOW DIMENSION";
    private final static String GAME_OVER = "GAME OVER!";
    private final static String WINNING_MESSAGE = "CONGRATULATIONS!";
    private final Image BACKGROUND_IMAGE = new Image("res/background0.png");
    private final static String WALL_IMAGE = "res/wall.png";
    private final static String CSV_PATH = "res/level0.csv";
    private final static String PLAYER = "Player";
    private final static String WALL = "Wall";
    private final static String SINKHOLE = "Sinkhole";
    private final static String TOP_LEFT = "TopLeft";
    private final static String BOTTOM_RIGHT = "BottomRight";
    private boolean gameStart;
    private final Font GAME_TITLE_FONT = new Font("res/frostbite.ttf", 75);
    private final int GAME_TITLE_FONT_SIZE = 75;
    private final Font GAME_MESSAGE_FONT = new Font("res/frostbite.ttf", 40);
    private ArrayList<StaticObject> walls = new ArrayList<>();
    private ArrayList<SinkHole> sinkHoles = new ArrayList<>();
    private Player player;
    private Point bottomRight;
    private Point topLeft;
    private boolean gameOver;
    private boolean gameWin;


    public ShadowDimension(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        gameStart = false;
        gameOver = false;
        gameWin = false;
        this.readCSV(CSV_PATH);

    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDimension game = new ShadowDimension();
        game.run();
    }

    /**
     * Method used to read file and create objects (You can change this
     * method as you wish).
     * Open file, read each line, split line into its components then create appropriate entities
     */
    private void readCSV(String filepath){
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            while((line = br.readLine()) != null) {
                // Split the line by comma
                String[] elements = line.split(",");

                if (elements[0].equals(PLAYER)) {
                    player = new Player(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]));
                } else if (elements[0].equals(WALL)){
                    walls.add(new StaticObject(WALL_IMAGE, Integer.parseInt(elements[1]), Integer.parseInt(elements[2])));
                } else if (elements[0].equals(SINKHOLE)) {
                    sinkHoles.add(new SinkHole(Integer.parseInt(elements[1]), Integer.parseInt(elements[2])));
                } else if (elements[0].equals(TOP_LEFT)) {
                    topLeft = new Point(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]));
                } else if (elements[0].equals(BOTTOM_RIGHT)) {
                    bottomRight = new Point(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]));
                }

            }
        } catch (IOException e) {
            // I/O output operation failure or interruption.
            // e.g. File does not exist
            e.printStackTrace(); // prints stack trace for exception to System.err
        } catch (NumberFormatException e) {
            // Exception is thrown whn a method that is supposed to convert a string to a number receives a string that it cannot convert.
            System.out.println("Number format error in the CSV file: " + e.getMessage());

        }

    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        // The starting screen
        if (!gameStart) {
            GAME_TITLE_FONT.drawString(GAME_TITLE,260 , 175 );
            GAME_MESSAGE_FONT.drawString("PRESS SPACE TO START", 350, 365);
            GAME_MESSAGE_FONT.drawString("USE ARROW KEYS TO FIND GATE", 350, 405);

            if (input.wasPressed(Keys.SPACE)) {
                gameStart = true;
            }

        }

        // Gameplay starting
        if (gameStart && !gameOver && !gameWin) {
            BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
            player.update(input, this);

            for (StaticObject wall : walls) {
                wall.update();
            }

            for (SinkHole hole : sinkHoles) {
                if (hole.getIsActive()) {
                    hole.update();
                }
            }

            if (player.isDead()) {
                gameOver = true;
            }
        }

        if (gameOver && !gameWin) {
            drawMessage(GAME_OVER);
        }

        if (gameWin) {
            drawMessage(WINNING_MESSAGE);
        }



    }

    // Check for collisions
    public void checkCollisions(Player player) {
        Rectangle fae = new Rectangle(player.getCurrentPosition(), player.getImage().getWidth(),
                                        player.getImage().getHeight());

        for (StaticObject wall : walls) {
            if (fae.intersects(wall.getBoundingBox())) {
                player.moveBack();
            }
        }

        for (SinkHole hole : sinkHoles) {
            if (fae.intersects(hole.getBoundingBox()) && hole.getIsActive()) {
                player.setHealth(hole.getDamagePoints());
                hole.setIsActive(false);
                // fae is damaged
            }
        }

    }

    public void checkOutOfBounds(Player player) {
        Point currentPosition = player.getCurrentPosition();

        if ((currentPosition.x > bottomRight.x) || (currentPosition.x < topLeft.x) || (currentPosition.y > bottomRight.y)
                || (currentPosition.y < topLeft.y)) {
            player.moveBack();
        }

    }

    public void checkReachedGate(Player player) {
        Point currentPosition = player.getCurrentPosition();

        if (currentPosition.x >= 950 && currentPosition.y >= 670) {
            gameWin = true;
        }
    }

    private void drawMessage(String message) {
        GAME_TITLE_FONT.drawString(message, (Window.getWidth()/2.0 - (GAME_TITLE_FONT.getWidth(message)/2.0)),
                (Window.getHeight()/2.0 + (GAME_TITLE_FONT_SIZE/2.0)));
    }

}
