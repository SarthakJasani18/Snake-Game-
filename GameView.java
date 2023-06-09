package resources.views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import resources.GameController;
import resources.model.*;

import java.awt.*;

/**
 * The visual functions are found here. Retrieves data from the models
 */
public class GameView {

    public Canvas gridCanvas;
    public GraphicsContext gc;

    public Label gameOverLabel;
    public Label gameWonLabel;
    public Label scoreLabel;
    public Button viewHighScore;
    private boolean gameOverVis = false;
    private boolean gameWonVis = false;

    private Game game;
    private double initX;
    private double initY;
    private double fullScale = 1;
    private boolean fullsize = false;
    private boolean gameRunning = false;
    private HighScore highScore;
    public Label highScoreLabel = new Label();
    private boolean highScoreVis = false;
    public TextField userName = new TextField();
    private int[][] turnArray = new int[MainMenuController.getxValue()][MainMenuController.getyValue()];

    public GameView(Game game) {
        this.game = game;
        highScore = new HighScore();
    }

    public void makeScene(Scene scene, Snake snek, Apple apple) {
        Scene gameScene = scene;
        gridCanvas = new Canvas(gameScene.getWidth(), gameScene.getHeight());
        initX = scene.getWidth();
        initY = scene.getHeight();
        gc = gridCanvas.getGraphicsContext2D();
        drawGrid(apple, snek, gc);

        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setId("gameOverLabel");
        String css = "resources/views/styling.css";
        gameOverLabel.getStylesheets().add(css);
        gameOverLabel.setVisible(false);

        gameWonLabel = new Label("YOU WON!");
        gameWonLabel.setId("gameWonLabel");
        gameWonLabel.getStylesheets().add(css);
        gameWonLabel.setVisible(false);

        scoreLabel = new Label("Score: " + game.getScorePoints());
        scoreLabel.setId("scoreLabel");
        scoreLabel.getStylesheets().add(css);
        scoreLabel.setFont(new Font("Arial", 20));
        scoreLabel.setVisible(true);

        viewHighScore = new Button();
        viewHighScore.setId("highScoreButton");
        viewHighScore.getStylesheets().add(css);
        viewHighScore.setText("view high score");
        viewHighScore.setOnAction(event -> {
            viewHighScore.setVisible(false);
            showHighScore(true);
        });
        viewHighScore.setVisible(false);

        userName = new TextField("Navn");
        userName.setId("userNameTextField");
        userName.getStylesheets().add(css);
        userName.setVisible(false);
        userName.setAlignment(Pos.CENTER);
        addTextLimiter(userName, 5);
        userName.setMaxWidth(100);

        highScoreLabel = new Label("HIGH SCORE \n");
        highScoreLabel.setId("highScoreLabel");
        highScoreLabel.getStylesheets().add(css);
        highScoreLabel.setVisible(false);
    }

    /**
     *Scales the game's graphics to full screen
     */
    public void scaleToFullscreen(boolean fullScreen, Stage primaryStage, Obstacle obstacle, Snake snek, Apple apple) {
        primaryStage.setFullScreen(fullScreen);

        double xScaling = Screen.getPrimary().getVisualBounds().getHeight() / initY;
        double yScaling = Screen.getPrimary().getVisualBounds().getWidth() / initX;

        if (fullScreen && !fullsize) {
            if (xScaling < yScaling) {
                fullScale = xScaling;
            } else {
                fullScale = yScaling;
            }
            gridCanvas.setWidth(gridCanvas.getWidth() * fullScale);
            gridCanvas.setHeight(gridCanvas.getHeight() * fullScale);
            fullsize = true;
        } else if (!fullScreen) {
            fullScale = 1;
            gridCanvas.setWidth(initX);
            gridCanvas.setHeight(initY);
            fullsize = false;
        }

        // gameover/gamewon labels font størrelse scaler alt efter om der er fullscreen eller ej
        gameOverLabel.setFont(new Font("Arial", 25 * fullScale));
        gameWonLabel.setFont(new Font("Arial", 25 * fullScale));
        drawGrid(apple, snek, obstacle, gc);
    }

    /**
     * Updates the path based on the position of the snake and the apple.
     */
    public void drawGrid(Apple apple, Snake snek, Obstacle obstacle, GraphicsContext gc) {
        // Tegner baggrunden
        gc.setFill(game.getColor());
        gc.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        // Tegner æblet
        gc.drawImage(apple.getImage(), (apple.getPos().x * game.getMultiplier()) * fullScale, (apple.getPos().y * game.getMultiplier()) * fullScale, game.getMultiplier() * fullScale - 3 * fullScale, game.getMultiplier() * fullScale - 3 * fullScale);

        // Tegner slangens krop
        int bodyIndex = 0;
        for (Point point : snek.getBody()) {
            drawBody(snek, point, bodyIndex);
            bodyIndex++;
        }

        //Tegner slangens hoved
        drawHead(snek);

        //Tegner forhindringerne
        gc.setFill(obstacle.getColor());
        for (Point point : obstacle.getObstacles()) {
            gc.fillRect(point.x * game.getMultiplier() * fullScale, point.y * game.getMultiplier() * fullScale, game.getMultiplier() * fullScale, game.getMultiplier() * fullScale);
        }
    }

    private void drawGrid(Apple apple, Snake snek, GraphicsContext gc) {
        // Tegner baggrunden
        gc.setFill(game.getColor());
        gc.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        // Tegner æblet
        gc.drawImage(apple.getImage(), (apple.getPos().x * game.getMultiplier()) * fullScale, (apple.getPos().y * game.getMultiplier()) * fullScale, game.getMultiplier() * fullScale - 3 * fullScale, game.getMultiplier() * fullScale - 3 * fullScale);

        // Tegner slangens krop
        int bodyIndex = 0;
        for (Point point : snek.getBody()) {
            drawBody(snek, point, bodyIndex);
            bodyIndex++;
        }

        //Tegner slangens hoved
        drawHead(snek);
    }

    /**
     * This function draws the snake's head and rotates it when rotated
     *
     * @param sneak the snake
     */
    private void drawHead(Snake snek) {
        snek.getImageViewHead().setRotate(snek.getSnakeDir());
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = snek.getImageViewHead().snapshot(params, null);
        gc.drawImage(rotatedImage, (snek.getHead().x * game.getMultiplier()) * fullScale, (snek.getHead().y * game.getMultiplier()) * fullScale, game.getMultiplier() * fullScale * fullScale, game.getMultiplier() * fullScale * fullScale);
    }

    /**
     * This part draws the snake's body tail and when it turns. The tail is placed at index 0, and rotates in relation to the rest of the body. The snake turns from a 2D array which
     * is made in {@link #turnDir(Snake) turnDir}.
     *
     * @param sneak the snake
     * @param points
     * @param index Which part of the hose to draw
     */
    private void drawBody(Snake snek, Point point, int index) {
        Image rotatedImage;
        if (snek.getBody().size() <= 1) {
            snek.getImageViewTail().setRotate(snek.getSnakeDir());
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snek.getImageViewTail().snapshot(params, null);
        } else if (index == 0) {
            int rotateTail;
            if (snek.getBody().get(1).getX() == snek.getBody().get(0).getX() && snek.getBody().get(1).getY() == snek.getBody().get(0).getY() - 1) {
                rotateTail = 0;
            } else if (snek.getBody().get(1).getX() == snek.getBody().get(0).getX() && snek.getBody().get(1).getY() == snek.getBody().get(0).getY() + 1) {
                rotateTail = 180;
            } else if (snek.getBody().get(1).getX() == snek.getBody().get(0).getX() - 1 && snek.getBody().get(1).getY() == snek.getBody().get(0).getY()) {
                rotateTail = 270;
            } else {
                rotateTail = 90;
            }
            snek.getImageViewTail().setRotate(rotateTail);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snek.getImageViewTail().snapshot(params, null);
        } else if (index > 0 && (turnArray[(int) snek.getBody().get(index).getX()][(int) snek.getBody().get(index).getY()] != 0)) {
            snek.getImageViewTurn().setRotate(90 * turnArray[(int) snek.getBody().get(index).getX()][(int) snek.getBody().get(index).getY()]);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snek.getImageViewTurn().snapshot(params, null);
        } else {
            snek.getImageViewBody().setRotate(snek.getBodyDir(index));
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snek.getImageViewBody().snapshot(params, null);
        }

        if (turnArray[(int) snek.getBody().get(0).getX()][(int) snek.getBody().get(0).getY()] != 0) {
            turnArray[(int) snek.getBody().get(0).getX()][(int) snek.getBody().get(0).getY()] = 0;
        }

        gc.drawImage(rotatedImage, (point.x * game.getMultiplier()) * fullScale, (point.y * game.getMultiplier()) * fullScale, game.getMultiplier() * fullScale * fullScale, game.getMultiplier() * fullScale * fullScale);
    }

    /**
    * The method writes a value to a 2D array that represents where on the grid the snake should turn and to which side it is turned
     *
     * @param sneak the snake
     */
    public void turnDir(Snake snek) {
        if ((GameController.lastKey == KeyCode.LEFT && GameController.key == KeyCode.UP) || (GameController.lastKey == KeyCode.DOWN && GameController.key == KeyCode.RIGHT)) {
            turnArray[(int) snek.getHead().getX()][(int) snek.getHead().getY()] = 2;
        } else if ((GameController.lastKey == KeyCode.LEFT && GameController.key == KeyCode.DOWN) || (GameController.lastKey == KeyCode.UP && GameController.key == KeyCode.RIGHT)) {
            turnArray[(int) snek.getHead().getX()][(int) snek.getHead().getY()] = 3;
        } else if ((GameController.lastKey == KeyCode.RIGHT && GameController.key == KeyCode.UP) || (GameController.lastKey == KeyCode.DOWN && GameController.key == KeyCode.LEFT)) {
            turnArray[(int) snek.getHead().getX()][(int) snek.getHead().getY()] = 1;
        } else if ((GameController.lastKey == KeyCode.UP && GameController.key == KeyCode.LEFT) || (GameController.lastKey == KeyCode.RIGHT && GameController.key == KeyCode.DOWN)) {
            turnArray[(int) snek.getHead().getX()][(int) snek.getHead().getY()] = 4;
        } else turnArray[(int) snek.getHead().getX()][(int) snek.getHead().getY()] = 0;
    }

    /**
     * Toggles the gameOver Label if the game is lost
     */
    public void gameOver(Snake snek) {
        if (snek.getDead() && !gameOverVis) {
            gameOverVis = true;
            gameOverLabel.setVisible(true);
            viewHighScore.setVisible(true);
        } else if (!snek.getDead() && gameOverVis) {
            gameOverLabel.setVisible(false);
            gameOverVis = false;
        }
    }

    /**
     * Toggles the gameWon Label if the game is won
     */
    public void gameWon() {
        if (game.gameWon() && !gameWonVis) {
            gameWonVis = true;
            gameWonLabel.setVisible(true);
        } else if (!game.gameWon() && gameWonVis) {
            gameWonVis = false;
            gameWonLabel.setVisible(false);
        }
    }

    /**
     *Updates the scoreLabel with the latest score
     */
    public void updateScoreLabel() {
        scoreLabel.setText("Score: " + game.getScorePoints());
    }

    /**
     * Shows the High Score visually when a name has been entered ingame
     */
    private void showHighScore(boolean show) {
        if (!show) {
            highScoreVis = false;
            highScoreLabel.setVisible(false);
        } else if (show) {
            gameOverLabel.setVisible(false);
            gameWonLabel.setVisible(false);
            userName.setVisible(true);
            userName.setOnAction(event -> {
                userName.setVisible(false);
                highScore.readHighScoreFromFile();
                highScore.addScore(game, userName.getText().toUpperCase());
                printHighScore();
            });

        }
    }

    /**
     * Prints high score as a label, can for example be used in the main menu
     */
    private void printHighScore() {
        highScoreVis = true;
        StringBuilder scores = new StringBuilder();
        for (int i = 0; i < highScore.getTotalArray().length; i++) {
            scores.append(highScore.getTotalArray()[i][0]).append("\t  -\t").append(highScore.getTotalArray()[i][1]).append("\n");
        }
        highScoreLabel.setText("     HIGH SCORE \n" + scores);
        highScoreLabel.setVisible(true);
    }

    /**
     * Limits user input to the selected length
     *
     * @param field The text field to limit
     * @param textMaxLength How many characters can be in the text
     */
    private static void addTextLimiter(final TextField field, final int textMaxLength) {
        field.textProperty().addListener((ov, oldValue, newValue) -> {
            if (field.getText().length() > textMaxLength) {
                String s = field.getText().substring(0, textMaxLength);
                field.setText(s);
            }
        });
    }
}