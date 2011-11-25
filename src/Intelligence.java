import dictionary.Dictionary;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 17.11.11
 * Time: 17.19
 * To change this template use File | Settings | File Templates.
// */
public class Intelligence {

    public static final boolean rackEvaluation = false;
    public static final boolean balance = true;
    public static final boolean doubles = true;
    public static final boolean triples = true;
    public static final boolean specials = true;

    public static final boolean positionEvaluation = false;
    public static final boolean doubleWord = true;
    public static final boolean tripleWord = true;
    public static final boolean doubleLetter = true;
    public static final boolean tripleLetter = true;
    public static final boolean weighting = true;

    public static final boolean firstTurnOpenness = true;
    public static final boolean duplicateProbability = true;

    public static Placement getPlacement(Dictionary dict, Board board, String rack) {
        ArrayList<Placement> candidates = getCandidates(dict, board, rack);
        return useHeuristics(board, rack, candidates);
    }

    public static ArrayList<Placement> getCandidates(Dictionary dict, Board board, String rack) {
        ArrayList<Placement> candidates = new ArrayList<Placement>();
        List<Character> charList = new ArrayList<Character>();
        for (char c : rack.toCharArray()) {
            charList.add(c);
        }
        char[] lockedLetters = new char[15];
	    for(int i = 0; i < lockedLetters.length; i++){
		    lockedLetters[i] = '_';
	    }
        // Horizontal computations
        for (int rw = 0; rw < 15; rw++) {
            for(int i = 0; i < lockedLetters.length; i++){
		    lockedLetters[i] = '_';
	        }
            for (int cl = 0; cl < 15; cl++) {
                if (board.getField(rw,cl).getLetter() != ' ') {
                    lockedLetters[cl] = board.getField(rw,cl).getLetter();
                }
            }
            List<String> []candList = dict.getWords(charList, board, rw, true);
            for (int i = 0; i < candList.length; i++) {
                if (candList[i] != null && candList[i].size() > 0) {
                    System.out.println(candList[i].size());
                    for (int k = 0; k < candList[i].size(); k++) {
                        String word = candList[i].get(k);
                        int index = i;
                        String theRack = "";
                        int counter = 0;
                        for (int j = index; j < word.length() + index; j++) {
                            if (board.getField(rw, j).getLetter() != word.charAt(counter)) {
                                theRack += word.charAt(counter);
                            }
                            counter++;
                        }
                        candidates.add(new Placement(word, rack, rw, index,  true));
                    }
                }
            }
        }

        // Vertical computations
        for (int cl = 0; cl < 15; cl++) {
            for(int i = 0; i < lockedLetters.length; i++){
		        lockedLetters[i] = '_';
	        }
            for (int rw = 0; rw < 15; rw++) {
                if (board.getField(rw,cl).getLetter() != ' ') {
                    lockedLetters[rw] = board.getField(rw,cl).getLetter();
                }
            }
            List<String> []candList = dict.getWords(charList, board, cl, false);
            for (int i = 0; i < candList.length; i++) {
                if (candList[i] != null && candList[i].size() > 0) {
                    for (int k = 0; k < candList[i].size(); k++) {
                        String word = candList[i].get(k);
                        int index = i;
                        String theRack = "";
                        int counter = 0;
                        for (int j = index; j < word.length() + index; j++) {
                            if (board.getField(cl, j).getLetter() != word.charAt(counter)) {
                                theRack += word.charAt(counter);
                            }
                            counter++;
                        }
                    candidates.add(new Placement(word, rack, cl, index,  false));
                    }

                }
            }
        }
        return candidates;
    }

    public static void main(String[] args){
        Dictionary dict = new Dictionary();
        Board board = new Board();
        System.out.println(board);
        board.addWord(new Placement("something", 7, 0, true));
        board.addWord(new Placement("test", 7, 4, false));
        System.out.println(board);
        ArrayList<Placement> placements = getCandidates(dict, board, "abcdefg");
        System.out.println(placements.size());
        for (int i = 0; i < placements.size(); i++) {
            System.out.print(placements.get(i));
        }
        System.out.println("I got this far");
        System.out.println(getPlacement(dict, board, "abcdefg"));
      }

    public static Placement useHeuristics(Board board, String rack, ArrayList<Placement> candidates) {
        Hashtable<Placement, Integer> candToScore = new Hashtable<Placement, Integer>();

        ArrayList<Candidate> candScores = new ArrayList<Candidate>();
        for (Placement candidate : candidates) {
            candScores.add(new Candidate(candidate, board.computeScore(candidate)));
        }

        // NOTE: maybe smart to sort by raw score and then only compute (expensive) heuristic calculations
        //       on subset (for instance 20 - 30) of candidates

        for (Placement candidate : candidates) {
            int score = board.computeScore(candidate);
            candToScore.put(candidate, score);

            if (rackEvaluation) {
                // Compute rackLeave with candidate placement
                String rackLeave = rack;
                for (int i = 0; i < candidate.getRack().length(); i++) {
                    if (rack.contains(Character.toString(candidate.getRack().charAt(i)))) {
                        rackLeave.replaceFirst(Character.toString(candidate.getRack().charAt(i)),"");
                    }
                }
            }
            if (positionEvaluation) {

            }
            // (...)

        }
        Placement theBest = null;
        int theBestScore = Integer.MIN_VALUE;
        for (Placement candidate : candToScore.keySet()) {
            if (candToScore.get(candidate) > theBestScore) {
                theBest = candidate;
                theBestScore = candToScore.get(candidate);
            }
        }
        return theBest;
    }
}