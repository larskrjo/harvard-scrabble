package logic;

import dictionary.Dictionary;
import dictionary.Direction;

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

    public static Placement getFirstPlacement(Dictionary dict, Board board, String rack) {
        List<String> words = new ArrayList<String>();
        List<Character> chars = new ArrayList<Character>();
        for (int i = 0; i < rack.length(); i++) {
            chars.add(rack.charAt(i));
        }
        for (int i = 0; i < rack.length(); i++) {
            char removed = chars.remove(0);
            Board tempboard = new Board();
            tempboard.addWord(new Placement(Character.toString(removed), 0, 0, Direction.HORIZONTAL));
            List<String> []tempwords = dict.getWords(chars, board.getGrid(), 0, Direction.HORIZONTAL);
            if (tempwords[0] != null) {
               for (int k = 0; k < tempwords[0].size(); k++) {
                words.add(tempwords[0].get(k));
               }
            }
            chars.add(removed);
        }

        for (int i = 0; i < words.size(); i++) {
            //System.out.println(words.get(i));
        }

        return null;
    }

    public static Placement getPlacement(Dictionary dict, Board board, String rack, boolean future) {
        ArrayList<Placement> candidates = getCandidates(dict, board, rack);
        return useHeuristics(dict, board, rack, candidates, future);
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
            //List<String> []candList = dict.getWords(charList, board, rw, true);
            List<String> []candList = dict.getWords(charList, board.getGrid(), rw, Direction.HORIZONTAL);
            for (int i = 0; i < candList.length; i++) {
                if (candList[i] != null && candList[i].size() > 0) {
                    //System.out.println(candList[i].size());
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
                        if (word.length() != theRack.length()) {
                            candidates.add(new Placement(word, theRack, rw, index,  Direction.HORIZONTAL));
                        }
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
            //List<String> []candList = dict.getWords(charList, board, cl, false);
            List<String> []candList = dict.getWords(charList, board.getGrid(), cl, Direction.VERTICAL);
            for (int i = 0; i < candList.length; i++) {
                if (candList[i] != null && candList[i].size() > 0) {
                    for (int k = 0; k < candList[i].size(); k++) {
                        String word = candList[i].get(k);
                        int index = i;
                        String theRack = "";
                        int counter = 0;
                        for (int j = index; j < word.length() + index; j++) {
                            if (board.getField(j, cl).getLetter() != word.charAt(counter)) {
                                theRack += word.charAt(counter);
                            }
                            counter++;
                        }
                        if (word.length() != theRack.length()) {
                            candidates.add(new Placement(word, theRack, index, cl,  Direction.VERTICAL));
                        }
                    }

                }
            }
        }
        return candidates;
    }

    public static void main(String[] args){
        Dictionary dict = new Dictionary();
        Board board = new Board();
        getFirstPlacement(dict, board, "detests");
        /*
        System.out.println(board);
        board.addWord(new Placement("something", 7, 0, Direction.HORIZONTAL));
        board.addWord(new Placement("test", 7, 4, Direction.VERTICAL));
        System.out.println(board);
        ArrayList<Placement> placements = getCandidates(dict, board, "abcdefg");
        System.out.println(placements.size());
        for (int i = 0; i < placements.size(); i++) {
            System.out.print(placements.get(i));
        }
        System.out.println("I got this far");
        System.out.println(getPlacement(dict, board, "abcdefg"));
        */
      }

    public static Placement useHeuristics(Dictionary dict, Board board, String rack, ArrayList<Placement> candidates, boolean futureValue) {
        Hashtable<Placement, Integer> candToScore = new Hashtable<Placement, Integer>();

        ArrayList<Candidate> candScores = new ArrayList<Candidate>();
        for (Placement candidate : candidates) {
            candScores.add(new Candidate(candidate, board.computeScore(candidate)));
        }
        java.util.Collections.sort(candScores);
        // Only compute heuristics on short-list of candidates
        for (int i = 0; i < candScores.size(); i++) {
            if (i > 30) {
                candScores.remove(i);
            }
        }

        for (int i = 0; i < candScores.size(); i++) {
            //System.out.println(candScores.get(i));
        }

        for (Placement candidate : candidates) {
            int score;
            if (futureValue) {
                score = board.computeScore(candidate) + placementFutureValue(dict, board, candidate);
            } else {
                score = board.computeScore(candidate);
            }
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
                // 1 - weighted distance from tripple word bonus / double word bonus and which letter facilitates placement
                //
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

    public static String rackExchange(Bag bag, String rack) {
        // Computes the optimal number of letters in rack for exchange given bag
        return rack;
    }

    public static int placementFutureValue(Dictionary dict, Board board, Placement placement) {
        if (placement == null) {
            return 0;
        }
        int value = 0;
        double kicker = 1.1;
        int word_weight = 7;
        int row = placement.getRow();
        int col = placement.getCol();
        String word = placement.getWord();
        double partValue = 0;
        for (int i = 0; i < word.length(); i++) {
            for (int j = 0; j < 15; j++) {
                if (placement.getDirection() == Direction.HORIZONTAL) {
                    if (board.getField(j, col + i).getHotspot() != ' ' && board.getField(j,col + i).getLetter() == ' ' && j != row) {
                        partValue += kicker*Double.parseDouble("" + board.getField(j, col+i).getHotspot())*Score.letterScore(word.charAt(i))/Math.abs(row - j);
                    }
                } else {
                    if (board.getField(row + i, j).getHotspot() != ' ' && board.getField(row + i, j).getLetter() == ' ' && j != col) {
                        partValue += kicker*Double.parseDouble("" + board.getField(row + i, j).getHotspot())*Score.letterScore(word.charAt(i))/Math.abs(col - j);
                    }
                }
            }
        }
        System.out.println(partValue);
        value -= partValue;

        int count = 0;
        int hots = 0;
        if (placement.getDirection() == Direction.HORIZONTAL) {
            for (int i = row + word.length(); i < 15; i++) {
                if (board.getField(i, col).getLetter() == ' ') {
                    count += 1;
                    if(board.getField(i, col).getHotspot() != ' ') {
                        hots += Integer.parseInt("" + board.getField(i,col).getHotspot());
                    }
                } else {
                    count = 0;
                    break;
                }
            }
        } else {
            for (int i = col + word.length(); i < 15; i++) {
                if (board.getField(row, i).getLetter() == ' ') {
                    count += 1;
                    if(board.getField(row, i).getHotspot() != ' ') {
                        hots += Integer.parseInt("" + board.getField(row, i).getHotspot());
                    }
                } else {
                    count = 0;
                    break;
                }
            }
        }
        if (count > 0) {
            List<String> potential_words = dict.getExtendedWords(word, count);
            int count_value = (potential_words.size()/word_weight + hots);
            if (count_value < 5) {
                value -= (potential_words.size()/word_weight + hots);
            } else {
                value -= 5;
            }
        }
        return value;
    }
}