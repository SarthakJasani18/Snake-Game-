package resources.model;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * All the technical aspects of the HighScore model are made in this class
 */
public class HighScore {
    private String path = "HighScore.txt";

    private boolean newHighScore = false;
    private String[][] totalArray = new String[10][2];

    public HighScore() {
            for (int i = 0; i < 10; i++) {
                totalArray[i][1] = "0";
            }
            for (int i = 0; i < 10; i++) {
                totalArray[i][0] = "AAAAA";
            }
    }

    /**
     * This method reads the text file with all the highscores.
     */
    public void readHighScoreFromFile() {
        File file = new File("HighScore.txt");

        int id = 0;

        try {
            if (file.createNewFile()) {

            } else {
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) { // tjekker om der er en ny linje
                    String next = sc.nextLine(); // scanner næste linje
                    if (id % 2 == 0) {
                        totalArray[id / 2][0] = next;
                    } else {
                        totalArray[id / 2][1] = next;
                    }
                    id++;
                }
                sc.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sortArray();
    }

    /**
     * Adds the score to the array with the other scores
     *
     * @param userName
     */
    public void addScore(Game game, String userName) {
        sortArray();
        if (game.getScorePoints() > Integer.parseInt(totalArray[9][1])) { //TODO flyt til et bedre sted
            newHighScore = true;
            String newScore = Integer.toString(game.getScorePoints());
            totalArray[9][0] = userName;
            totalArray[9][1] = newScore;
        }
        sortArray();
        try {
            writeNewHighScore();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The function compares the score that is in the array,
     * and sorts based on this so that the name matches the score
     * after it, the sort is in ascending order
     */
    public void sortArray() {
        Arrays.sort(totalArray, (first, second) -> Integer.valueOf(second[1]).compareTo(Integer.valueOf(first[1])));

    }

  
    private void writeNewHighScore() throws IOException {
        File file = new File(path).getAbsoluteFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < totalArray.length; i++) {
            writer.write(String.valueOf(totalArray[i][0]));
            writer.newLine();
            writer.write(String.valueOf(totalArray[i][1]));
            if (i != totalArray.length - 1) writer.newLine();
        }
        writer.close();
    }

    public String[][] getTotalArray() {
        return totalArray;
    }
}

