package logic;

/**
 * Created by IntelliJ IDEA.
 * User: PalCSNjolstad
 * Date: 05.12.11
 * Time: 21.19
 * To change this template use File | Settings | File Templates.
 */
public class LetterScore implements Comparable<LetterScore>{

    private char c;
    private double score;

    public LetterScore(char c, double score) {
        this.c = c;
        this.score = score;
    }

    public char getChar() {
        return this.c;
    }

    public double getScore() {
        return this.score;
    }

    public int compareTo(LetterScore other) {
        if (this.getScore() > other.getScore()) {
            return 1;
        } else if (this.getScore() < other.getScore()) {
            return -1;
        } else {
            return 0;
        }
    }
}


