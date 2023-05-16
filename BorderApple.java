package resources.model;

import javafx.scene.image.Image;

/** Bonus apple that removes the edges of the game (the removal is done in the controller) */
public class BorderApple extends Apple {
    private Image image = new Image("resources/views/res/snakeapplerainbow.png");

    public BorderApple(int x, int y, Snake snek, Obstacle obstacle) {
        super(x, y, snek, obstacle);
    }

    public int getPoints() {
        int points = 5;
        return points;
    }

    public Image getImage() {
        return image;
    }
}
