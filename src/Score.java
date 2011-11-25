/**
 * Created by IntelliJ IDEA.
 * User: PalCSNjolstad
 * Date: 16.11.11
 * Time: 22.34
 * To change this template use File | Settings | File Templates.
 */
public class Score {

    // Returns score for letter according to English official Scrabble
    public static int letterScore(char ltr) {
        if (ltr == 'e' || ltr == 'a' || ltr == 'i' || ltr == 'o' || ltr == 'n' || ltr == 'r' || ltr == 't' || ltr == 'l' || ltr == 's' || ltr == 'u') {
            return 1;
        } else if (ltr == 'd' || ltr == 'g') {
            return 2;
        } else if (ltr == 'b' || ltr == 'c' || ltr == 'm' || ltr == 'p') {
            return 3;
        } else if (ltr == 'f' || ltr == 'h' || ltr == 'v' || ltr == 'w' || ltr == 'y') {
            return 4;
        } else if (ltr == 'k') {
            return 5;
        } else if (ltr == 'j' || ltr == 'x') {
            return 8;
        } else if (ltr == 'q' || ltr == 'z') {
            return 10;
        } else {
            return -1;
        }
    }

    public static double rackWeight(String word) {
        double score = 0;
        char[] letters = word.toCharArray();
        for (char letter : letters) {
            if (isVowel(letter)) {
                score += 1;
            } else {
                score -= 1;
            }
        }
        score = score/letters.length;
        return score;
    }

    public static boolean isVowel(char letter) {
        if (letter == 'a' || letter == 'e' || letter == 'i' || letter == 'o' || letter == 'u' || letter == 'y') {
            return true;
        } else {
            return false;
        }
    }

}
