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
        for (int i = 0; i < 12; i++) {
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
}
