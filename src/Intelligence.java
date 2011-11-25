import com.sun.corba.se.spi.ior.MakeImmutable;
import dictionary.Dictionary;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 17.11.11
 * Time: 17.19
 * To change this template use File | Settings | File Templates.
// */
public class Intelligence {

    public static Placement getPlacement(Dictionary dict, Board board, String rack) {
        ArrayList<Placement> candidates = getCandidates(dict, board, rack);
        return null;
    }

    public static ArrayList<Placement> getCandidates(Dictionary dict, Board board, String rack) {
        ArrayList<Placement> candidates = new ArrayList<Placement>();
        ArrayList<Tuple> positions = board.getLetterPositions();
        for (Tuple position : positions) {
            candidates.addAll(getCandidatesSubset(dict, board, rack, position));
        }
        return candidates;
    }

    public static ArrayList<Placement> getCandidatesSubset(Dictionary dict, Board board, String rack, Tuple position) {
        ArrayList<Placement> candidatesSubset = new ArrayList<Placement>();
        //candidatesSubset.addAll(getCandidatesSubsetHorizontal(dict, board, rack, position));
        //candidatesSubset.addAll(getCandidatesSubsetVertical(dict, board, rack, position));
        return candidatesSubset;
    }

    public static int evalFunction(String word) {
        return simpleGreedy(word);
    }

    //public static ArrayList<Placement> getCandidatesSubsetHorizontal(Dictionary dict, Board board, String rack, Tuple position) {
        // Find open spaces (up to seven or space before first letter) left of position and right (up to seven) of position

        //for (int i = 0; i <= 8; i++) {

      //  }
    //}

    //public static ArrayList<Placement> getCandidatesSubsetVertical(Dictionary dict, Board board, String rack, Tuple position) {
        // Todo
    //}

    //public ArrayList<Placement>

    public static int simpleGreedy(String word) {
	    int score = 0;
	    char[] letters = word.toCharArray();
	    for (char letter : letters) {
		    score += Score.letterScore(letter);
	    }
	    return score;
    }

    public static void main(String[] args){

      }

}
