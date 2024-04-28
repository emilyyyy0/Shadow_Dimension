import bagel.Font;
import bagel.Image;
import bagel.util.Point;
import bagel.*;

/**
 * Abstract level class, used by Level0 and Level1.
 */

public abstract class Level {
    protected Image background;
    protected static Font font;
    protected final int INSTRUCTION_FONT_SIZE = 40;
    protected final int TITLE_FONT_SIZE = 75;
    protected final Font TITLE_FONT = new Font("res/frostbite.ttf", TITLE_FONT_SIZE);
    protected final static String END_MESSAGE = "GAME OVER!";
    protected Player player;
    protected Point topLeft;
    protected Point bottomRight;
    protected boolean gameOver = false;
    protected boolean hasStarted = false;
    protected boolean playerWin = false;
    protected boolean isLevel0Complete = false;

    /**
     * Reads CSV file and creates new objects.
     */
    public abstract void readCSV();

    /**
     * Performs update on the state of the level
     * @param input: Input from the keyboard.
     */
    public abstract void update(Input input);

    /**
     * Method that checks the players collisions with object inside the game.
     * @param player: The player Fae.
     */
    public abstract void checkCollisions(Player player);

    /**
     * Method that checks if player is going out of the screen boundary.
     * @param player: The player Fae
     */
    public void checkOutOfBounds(Player player){
        Point currentPosition = player.getPosition();
        if((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeft.y) || (currentPosition.x < topLeft.x) || (currentPosition.x > bottomRight.x)){
            player.moveBack();
        }
    }

    /**
     * Method that renders
     * @param message: String
     */
    public void drawMessage(String message){
        font.drawString(message, (Window.getWidth()/2.0 - (font.getWidth(message)/2.0)), (Window.getHeight()/2.0 + TITLE_FONT_SIZE/2.0));
    }

    /**
     * Renders the end message of the level and when the player is dead.
     * @param message: String
     */
    public void drawEndScreen(String message){
        TITLE_FONT.drawString(message,(Window.getWidth()/2.0 - (TITLE_FONT.getWidth(message)/2.0)),
                (Window.getHeight()/2.0 + (TITLE_FONT_SIZE/2.0)));
    }


}
