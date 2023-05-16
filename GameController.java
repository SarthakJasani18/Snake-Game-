package resources;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import resources.model.*;
import resources.views.GameView;
import resources.views.MainMenuController;

import java.io.File;
import java.util.Random;



public class GameController {
    private Snake snek;
    private Apple apple;
    private GameView gameView;
    private Game game;
    private Stage stage;
    private String coin = "resources/views/res/coinSound.wav";
    
    private Obstacle obstacle;
   
    public static KeyCode key = KeyCode.LEFT;
    public static KeyCode lastKey = KeyCode.LEFT;
    private boolean keyPressed = false;

   
    public GameController(Snake snek, Apple apple, Game game, GameView gameView, Obstacle obstacle, Stage stage) {
        this.snek = snek;
        this.apple = apple;
        this.gameView = gameView;
        this.game = game;
        this.stage = stage;

        this.obstacle = obstacle;
        animation.setCycleCount(Animation.INDEFINITE);
        animation.setRate(game.getSpeed());
    }

    
    private final Timeline animation = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
      
        if (snek.getDead()) {
            return;
        } else {

            if (key.equals(KeyCode.LEFT)) {
                snek.move('L');
            } else if (key.equals(KeyCode.RIGHT)) {
                snek.move('R');
            } else if (key.equals(KeyCode.UP)) {
                snek.move('U');
            } else if (key.equals(KeyCode.DOWN)) {
                snek.move('D');
            }

            if (!snek.getDead() && snek.ateApple(apple)) {
           
                powerApple();
                game.incScore();
                if (game.gameWon()) {
                    gameView.drawGrid(apple, snek, obstacle, gameView.gc);
                    return;
                }
                newApple();
            }

            keyPressed = false;

            if (!snek.getDead() || snek.hitSelf()) {
                gameView.drawGrid(apple, snek, obstacle, gameView.gc);
            }

            gameView.gameWon();
            gameView.gameOver(snek);
            gameView.updateScoreLabel();
        }
    }));

   
    public void handle(KeyEvent event) {
        if (event.getCode().isArrowKey()) animation.play();

        if (valKey(event.getCode()) && !keyPressed) {
            lastKey = key;
            key = event.getCode();
            keyPressed = true;
            gameView.turnDir(snek);
        }

       
        if (event.getCode().equals(KeyCode.P)) {
            animation.stop();
        }

        
        if (event.getCode().equals(KeyCode.TAB)) {
            reset();
        }


        if (event.getCode().equals(KeyCode.PLUS)) {
            gameView.scaleToFullscreen(true, stage, obstacle, snek, apple);
            gameView.highScoreLabel.setStyle("-fx-font-size: 50;");
            gameView.gameOverLabel.setStyle("-fx-font-size: 50;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 50;");
            gameView.scoreLabel.setStyle("-fx-font-size: 35;");
        }

 
        if (event.getCode().equals(KeyCode.MINUS)) {
            gameView.scaleToFullscreen(false, stage, obstacle, snek, apple);
            gameView.highScoreLabel.setStyle("-fx-font-size: 15;");
            gameView.gameOverLabel.setStyle("-fx-font-size: 15;");
            gameView.gameWonLabel.setStyle("-fx-font-size: 15;");
            gameView.scoreLabel.setStyle("-fx-font-size: 15;");
        }

        if (event.getCode().equals(KeyCode.ESCAPE)) {
            MainMenuController mainMenuController = new MainMenuController();
            mainMenuController.getViewManager().getPrimaryStage().show();
            mainMenuController.getViewManager().getGameStage().close();
        }
    }

    
    private boolean valKey(KeyCode key) {
        if (!key.isArrowKey()) return false;
        else {
            if (key.equals(KeyCode.LEFT)) if (!this.key.equals(KeyCode.RIGHT)) return true;
            if (key.equals(KeyCode.RIGHT)) if (!this.key.equals(KeyCode.LEFT)) return true;
            if (key.equals(KeyCode.DOWN)) if (!this.key.equals(KeyCode.UP)) return true;
            if (key.equals(KeyCode.UP)) return !this.key.equals(KeyCode.DOWN);
            return false;
        }
    }

   
    private void reset() {

    
        animation.jumpTo(Duration.ZERO);
        animation.stop();

     
        snek = new Snake(game.getWidth(), game.getHeight(), game.isNoBorder(), obstacle);
        apple = new Apple(game.getWidth(), game.getHeight(), snek, obstacle);

       
        animation.setRate(game.getSpeed());
        key = KeyCode.LEFT;
        keyPressed = false;
        game.resetScore();
        obstacle.reset();

        
        gameView.gameWon();
        gameView.gameOver(snek);
        gameView.updateScoreLabel();
        gameView.viewHighScore.setVisible(false);
        gameView.highScoreLabel.setVisible(false);

      
        gameView.drawGrid(apple, snek, obstacle, gameView.gc);
    }

    
    private void powerApple() {
        animation.setRate(game.getSpeed());
        snek.ignoreBorders(false);
        if (apple.getClass().equals(SpeedApple.class)) {
            SpeedApple tempApple = (SpeedApple) apple;
            animation.setRate(game.getSpeed() + tempApple.getSpeedBonus());
        } else if (apple.getClass().equals(ObstacleApple.class)){
            Random rnd = new Random();
            obstacle.newObstacles(snek, apple, rnd.nextInt(game.getWidth() * game.getHeight()/ 20) +3);
            game.decMaxScore(obstacle.getObstacles().size());
        } else if (apple.getClass().equals(BorderApple.class)) {
            snek.ignoreBorders(true);
        }
        game.addPoints(apple.getPoints());
    }

   
    private void newApple() {
        if (game.isPowerUp()) {
            Random rnd = new Random();
            int ran = rnd.nextInt(12);
            if (ran == 1) {
                apple = new ScoreApple(game.getWidth(), game.getHeight(), snek, obstacle);
            } else if (ran == 2) {
                apple = new SpeedApple(game.getWidth(), game.getHeight(), snek, obstacle);
            } else if (ran == 3) {
                apple = new BorderApple(game.getWidth(), game.getHeight(), snek, obstacle);
            } else if (ran == 4 && obstacle.getObstacles().size() == 0) {
                apple = new ObstacleApple(game.getWidth(), game.getHeight(), snek, obstacle);
            } else {
                apple = new Apple(game.getWidth(), game.getHeight(), snek, obstacle);
            }
        } else {
            apple = new Apple(game.getWidth(), game.getHeight(), snek, obstacle);
        }
    }
}