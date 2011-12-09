package logic;

import dictionary.Dictionary;
import dictionary.Direction;
import org.omg.CORBA.INITIALIZE;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 16.11.11
 * Time: 20.10
 * To change this template use File | Settings | File Templates.
 */
public class Board{

    private Field[][] grid;

    public Board() {
        // Initialize board
        this.grid = new Field[15][15];
        for (int rw = 0; rw < 15; rw++) {
            for (int cl = 0; cl < 15; cl++) {
                this.grid[rw][cl] = new Field(' ');
            }
        }
        // Add triple word fields
        int []rows4 = {0, 0 , 0, 7, 7, 14, 14, 14};
        int []cols4 = {0, 7 , 14, 0, 14, 0, 7, 14};
        for (int i = 0; i < rows4.length; i++) {
            this.grid[rows4[i]][cols4[i]].setHotspot('4');
        }
        // Add double word fields
        int []rows3 = {1, 1, 2, 2, 3, 3, 4, 4, 7, 10, 10, 11, 11, 12, 12, 13, 13};
        int []cols3 = {1, 13, 2, 12, 3, 11, 4, 10, 7, 4, 10, 3, 11, 2, 12, 1, 13};
        for (int i = 0; i < rows3.length; i++) {
            this.grid[rows3[i]][cols3[i]].setHotspot('3');
        }
        // Add triple letter fields
        int []rows2 = {1, 1, 5, 5, 5, 5, 9, 9, 9, 9, 13, 13};
        int []cols2 = {5, 9, 1, 5, 9, 13, 1, 5, 9, 13, 5, 9};
        for (int i = 0; i < rows2.length; i++) {
            this.grid[rows2[i]][cols2[i]].setHotspot('2');
        }
        // Add double letter fields
        int []rows1 = {0, 0, 2, 2, 3, 3, 3, 6, 6, 6, 6, 7, 7, 8, 8, 8, 8, 11, 11, 11, 12, 12, 14, 14};
        int []cols1 = {3, 11, 6, 8, 0, 7, 14, 2, 6, 8, 12, 3, 11, 2, 6, 8, 12, 0, 7, 14, 6, 8, 3, 11};
        for (int i = 0; i < rows1.length; i++) {
            this.grid[rows1[i]][cols1[i]].setHotspot('1');
        }
    }

    public Field getField(int rw, int cl) {
        return this.grid[rw][cl];
    }

    public int addWord(Placement placement) {
        String word = placement.getWord();
        int rw = placement.getRow();
        int cl = placement.getCol();

        // Adds a word to board. Returns the score if successful and -1 if placement is in conflict current board (not in terms of dictionary)
        if (placement.getDirection() == Direction.HORIZONTAL) {
            if (cl + word.length() > 15) {
                return -1;
            }
            for (int i = 0; i < word.length(); i++) {
                if (this.grid[rw][cl+i].getLetter() != ' ' && this.grid[rw][cl+i].getLetter() != word.charAt(i)) {
                    return -1;
                }
            }
            int score = computeScore(placement);
            for (int i = 0; i < word.length(); i++) {
                this.grid[rw][cl+i].setLetter(word.charAt(i));
            }
            return score;
        } else {
            if (rw + word.length() > 15) {
                return -1;
            }
            for (int i = 0; i < word.length(); i++) {
                if (this.grid[rw+i][cl].getLetter() != ' ' && this.grid[rw+i][cl].getLetter() != word.charAt(i)) {
                    return -1;
                }
            }
            int score = computeScore(placement);
            for (int i = 0; i < word.length(); i++) {
                this.grid[rw+i][cl].setLetter(word.charAt(i));
            }
            return score;
        }
    }

    public int computeScore(Placement placement) {
        String word = placement.getWord();
        int rw = placement.getRow();
        int cl = placement.getCol();
        // Computes the score of a given placement and -1 if placement is in conflict current board (not in terms of dictionary)
        int raw_score = 0;
        int other_score = 0;
        int letter_bonus = 0;
        int word_bonus = 1;
        int letters_placed = 0; // Used to determine whether full rack utilization bonus should be awarded
        int full_rack_bonus = 0;
        if (placement.getDirection() == Direction.HORIZONTAL) {
            // Computations for horizontal placement
            if (cl + word.length() > 14) {
                return -1;
            }
            for (int i = 0; i < word.length(); i++) {
                if (this.grid[rw][cl+i].getLetter() != ' ' && this.grid[rw][cl+i].getLetter() != word.charAt(i)) {
                    return -1;
                }
            }
            for (int i = 0; i < word.length(); i++) {
                int letter_score = Score.letterScore(word.charAt(i));
                raw_score += letter_score;
                if (this.grid[rw][cl+i].getLetter() == ' ') {
                    letters_placed++;
                    if (this.grid[rw][cl+i].getHotspot() == '1') {
                        letter_bonus = letter_bonus + letter_score*1;
                    } else if (this.grid[rw][cl+i].getHotspot() == '2') {
                        letter_bonus = letter_bonus + letter_score*2;
                    } else if (this.grid[rw][cl+i].getHotspot() == '3') {
                        word_bonus = word_bonus * 2;
                    } else if (this.grid[rw][cl+i].getHotspot() == '4') {
                        word_bonus = word_bonus * 3;
                    }
                }
                if (rw < 14 && this.grid[rw][cl+i].getLetter() == ' ' && this.grid[rw+1][cl+i].getLetter() != ' ') {
                    int k = 1;
                    while (rw + k <= 14 && this.grid[rw+k][cl+i].getLetter() != ' ') {
                        other_score += Score.letterScore(this.grid[rw + k][cl + i].getLetter());
                        k++;
                    }

                }
                if (rw > 0 && this.grid[rw][cl+i].getLetter() == ' ' && this.grid[rw-1][cl+i].getLetter() != ' ') {
                    int k = 1;
                    while (rw - k >= 0 && this.grid[rw-k][cl+i].getLetter() != ' ') {
                        other_score += Score.letterScore(this.grid[rw - k][cl + i].getLetter());
                        k++;
                    }
                }
            }
            // Full rack utilization bonus check
            if (letters_placed >= 7) {
                full_rack_bonus = 50;
            }
            return raw_score*word_bonus+letter_bonus+other_score + full_rack_bonus;
        } else {
            // Computations for vertical placement
            if (rw + word.length() > 14) {
                return -1;
            }
            for (int i = 0; i < word.length(); i++) {
                if (this.grid[rw+i][cl].getLetter() != ' ' && this.grid[rw+i][cl].getLetter() != word.charAt(i)) {
                    return -1;
                }
            }
            for (int i = 0; i < word.length(); i++) {
                int letter_score = Score.letterScore(word.charAt(i));
                raw_score += letter_score;
                if (this.grid[rw+i][cl].getLetter() == ' ') {
                    letters_placed++;
                    if (this.grid[rw+i][cl].getHotspot() == '1') {
                        letter_bonus += letter_score*1;
                    } else if (this.grid[rw+i][cl].getHotspot() == '2') {
                        letter_bonus += letter_score*2;
                    } else if (this.grid[rw+i][cl].getHotspot() == '3') {
                        word_bonus *= 2;
                    } else if (this.grid[rw+i][cl].getHotspot() == '4') {
                        word_bonus *= 3;
                    }
                }
                if (cl < 14 && this.grid[rw+i][cl].getLetter() == ' ' && this.grid[rw+i][cl+1].getLetter() != ' ') {
                    int k = 1;
                    while (cl + k <= 14 && this.grid[rw+i][cl+k].getLetter() != ' ') {
                        other_score += Score.letterScore(this.grid[rw + i][cl + k].getLetter());
                        k++;
                    }

                }
                if (cl > 0 && this.grid[rw+i][cl].getLetter() == ' ' && this.grid[rw+i][cl-1].getLetter() != ' ') {
                    int k = 1;
                    while (cl - k >= 0 && this.grid[rw+i][cl-k].getLetter() != ' ') {
                        other_score += Score.letterScore(this.grid[rw + i][cl - k].getLetter());
                        k++;
                    }
                }
            }
            // Full rack utilization bonus check
            if (letters_placed >= 7) {
                full_rack_bonus = 50;
            }
            return raw_score*word_bonus+letter_bonus+other_score + full_rack_bonus;
        }
    }

    private void addLetter(char letter, int rw, int cl) {
        // Adds a letter to the board
        this.grid[rw][cl].setLetter(letter);
    }

    public ArrayList<Tuple> getLetterPositions() {
        ArrayList<Tuple> positions = new ArrayList<Tuple>();
        for (int rw = 0; rw < 15; rw++) {
            for (int cl = 0; cl < 15; cl++) {
                if (this.grid[rw][cl].getLetter() != ' ') {
                    positions.add(new Tuple(rw, cl));
                }
            }
        }
        return positions;
    }

    public String toString() {
        String string = "";
        for (int rw = 0; rw < 15; rw++) {
            string = string + "\n";
            for (int cl = 0; cl < 15; cl++) {
                string = string + grid[rw][cl].toString();
            }
        }
        return string;
    }

    public Field[][] getGrid() {
        return this.grid;
    }

    public List<Character> getAllCharactersOnBoard() {
        List<Character> chars = new ArrayList<Character>();
        for (int rw = 0; rw < 15; rw++) {
            for (int cl = 0; cl < 15; cl++) {
                if (this.getField(rw, cl).getLetter() != ' ') {
                    chars.add(this.getField(rw, cl).getLetter());
                }
            }
        }
        return chars;
    }

    public boolean isEmpty() {
        return this.getAllCharactersOnBoard().size() == 0;
    }
}
