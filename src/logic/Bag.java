package logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 17.11.11
 * Time: 17.21
 * To change this template use File | Settings | File Templates.
 */
public class Bag {
    public List<Character> letters;

    public Bag() {
        this.letters = new ArrayList<Character>();
        for (int i = 0; i < 30; i++) {
            this.letters.add('e');
            if (i > 2) {
                this.letters.add('i');
                this.letters.add('a');
            }
            if (i > 3) {
                this.letters.add('o');
            }
            if (i > 5) {
                this.letters.add('n');
                this.letters.add('r');
                this.letters.add('t');
            }
            if (i > 7) {
                this.letters.add('l');
                this.letters.add('s');
                this.letters.add('u');
                this.letters.add('d');
            }
            if (i > 8) {
                this.letters.add('g');
            }
            if (i > 9) {
                this.letters.add('b');
                this.letters.add('c');
                this.letters.add('m');
                this.letters.add('p');
                this.letters.add('f');
                this.letters.add('h');
                this.letters.add('v');
                this.letters.add('w');
                this.letters.add('y');
            }
            if (i > 10) {
                this.letters.add('k');
                this.letters.add('j');
                this.letters.add('x');
                this.letters.add('q');
                this.letters.add('z');
            }
        }
    }

    public char drawLetter() {
        int index = (int)(Math.random()*this.letters.size());
        char letter = this.letters.get(index);
        this.letters.remove(index);
        return letter;
    }

    public char[] drawPlayerStacks() {
        char[] letters = new char[7];
        for (int i = 0; i < 7; i++) {
            letters[i] = drawLetter();
        }
        return letters;
    }

    public boolean isEmpty() {
        return this.letters.isEmpty();
    }

    public char exchangeLetter(char letter) {
        if (isEmpty()) {
            return letter;
        }
        char ret = drawLetter();
        this.letters.add(letter);
        return ret;
    }

    public List<Character> getLetters() {
        return letters;
    }

    public String toString() {
        String ret = "";
        for (char letter : letters) {
            ret += letter;
        }
        return ret;
    }

    public static List<Character> getCharactersInGame() {
        return (new Bag()).letters;
    }

}
