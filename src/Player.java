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

    public Player(char[] letters) {
        this.letters = new ArrayList<Character>();
        for (char letter : letters) {
            this.letters.add(letter);
        }
        this.score = 0;
    }

    public char[] getLetters() {
        char[] list = new char[7];
        for (int i = 0; i < this.letters.size(); i++) {
            list[i] = this.letters.get(i);
        }
        return list;
    }

    public void removeLetter(char letter) {
        this.letters.remove(letter);
    }

    public boolean addLetter(char letter) {
        if (this.letters.size() < 7) {
            this.letters.add(letter);
            return true;
        }
        return false;
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
}
