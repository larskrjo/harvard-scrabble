package logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 16.11.11
 * Time: 20.11
 * To change this template use File | Settings | File Templates.
 */
public class Player {
    public List<Character> letters;
    public int score;
    public int passCount;

    public Player(char[] letters) {
        this.letters = new ArrayList<Character>();
        for (char letter : letters) {
            this.letters.add(letter);
        }
        this.score = 0;
        this.passCount = 0;
    }

    public String getLetters() {
        String word = "";
        for (int i = 0; i < this.letters.size(); i++) {
            word = word + this.letters.get(i);
        }
        return word;
    }

    public void removeWord(String str) {
        for (char letter : str.toCharArray()) {
            removeLetter(letter);
        }
    }

    public void removeLetter(char letter) {
        this.letters.remove(this.letters.indexOf(letter));
    }

    public boolean isRackFull() {
        return this.letters.size() == 7;
    }

    public void addLetter(char letter) {
        if (this.letters.size() < 7) {
            this.letters.add(letter);
        }
    }

    public boolean hasLetter(char letter) {
        return this.letters.contains(letter);
    }

    public int getScore() {
        return this.score;
    }

    public void addScore(int value) {
        this.score += value;
    }

    public void newPass() {
        this.passCount += 1;
    }

    public void clearPass() {
        this.passCount = 0;
    }

    public boolean passLimit() {
        return this.passCount >= 3;
    }
}
