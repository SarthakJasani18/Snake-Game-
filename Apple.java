package resources.model;

import javafx.scene.image.Image;

import java.awt.*;
import java.util.Random;


/** Model to contain information about
 * an apple instance. That is its position and color.
 */

public class Apple {
    private Point pos;
    private Image image = new Image("resources/views/res/snakeapple.png");
    /** The constructor creates a new position for
     * the apple until the position is not already taken.
     * @param x The width of the game.
     * @param y Height of the game.
     * @param snek The snake that the apple must not be placed on top of.
     */
    public Apple(int x, int y, Snake snek, Obstacle obstacle) {
        int tx;
        int ty;
        do {
            Random rnd = new Random();

            tx = rnd.nextInt(x);
            ty = rnd.nextInt(y);
            pos = new Point(tx, ty);
        } while (snek.getHead().equals(pos) || snek.getBody().contains(pos) || obstacle.getObstacles().contains(pos));
    }

    public Point getPos() {
        return new Point(pos);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    public Image getImage() {
        return image;
    }
}