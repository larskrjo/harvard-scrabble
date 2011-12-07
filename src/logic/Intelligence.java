package logic;

import dictionary.Dictionary;
import dictionary.Direction;

import java.util.*;

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

    public static Placement getPlacement(Dictionary dict, Board board, String rack, boolean future, boolean rackEval) {
        ArrayList<Placement> candidates = getCandidates(dict, board, rack);
        return useHeuristics(dict, board, rack, candidates, future, rackEval);
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
                        //if (word.length() != theRack.length()) {
                            candidates.add(new Placement(word, theRack, index, cl,  Direction.VERTICAL));
                        //}
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

    public static Placement useHeuristics(Dictionary dict, Board board, String rack, ArrayList<Placement> candidates, boolean futureValue, boolean rackEval) {
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
            int score = board.computeScore(candidate);
            if (futureValue) {
                score += placementFutureValue(dict, board, candidate);
            }
            if (rackEval) {
                score += rackEval(board, rack, candidate);
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

    public static int placementFutureValue(Dictionary dict, Board board, Placement placement) {
        if (placement == null) {
            return 0;
        }
        int value = 0;
        int word_weight = 10;
        int row = placement.getRow();
        int col = placement.getCol();
        String word = placement.getWord();
        double partValue = 0;
        for (int i = 0; i < word.length(); i++) {
            for (int j = -2; j < 3; j++) {
                if (placement.getDirection() == Direction.HORIZONTAL && row + j < 15 && row + j >= 0) {
                    if (board.getField(row + j, col + i).getHotspot() != ' ' && board.getField(row + j,col + i).getLetter() == ' ' && j != 0) {
                        if (Score.isVowel(word.charAt(i))) {
                            partValue += 0.3+1/Math.abs(j);
                            if(board.getField(row + j, col + i).getHotspot() == '2' || board.getField(row + j, col + i).getHotspot() == '4') {
                                if (word.charAt(i) == 'u' && j == -1) {
                                    partValue += 10;
                                } else {
                                    partValue += 2;
                                }
                            }
                        }
                    }
                } else if ((placement.getDirection() == Direction.VERTICAL && col + j < 15 && col+j >= 0)) {
                    if (board.getField(row + i, col + j).getHotspot() != ' ' && board.getField(row + i, col + j).getLetter() == ' ' && j != 0) {
                        if (Score.isVowel(word.charAt(i))) {
                            partValue += 0.3 + 1/Math.abs(j);
                            if (board.getField(row + i, col + j).getHotspot() == '2' || board.getField(row + i, col + j).getHotspot() == '4') {
                                if (word.charAt(i) == 'u' && j == 1) {
                                    partValue += 10;
                                } else {
                                    partValue += 2;
                                }
                            }
                        }
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
                        hots += 1;
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
                        hots += 1;
                    }
                } else {
                    count = 0;
                    break;
                }
            }
        }
        if (count > 0 && value > 0) {
            List<String> potential_words = dict.getExtendedWords(word, count);
            int count_value = (potential_words.size()/word_weight + hots);
            if (count_value < 3) {
                value -= (potential_words.size()/word_weight + hots);
            } else {
                value -= 3;
            }
        }
        return value;
    }

    /*

    Rack heuristics start here

     */

    public static double rackEval(Board board, String rack, Placement placement) {
        // Rack evaluation without consideration for what's left in the bag
        // Based on Sheppard (2002) Towards Perfect Game of Scrabble
        double heuristic = 0;
        double vowelCount = 0;
        double consCount = 0;
        List<Character> rackLeave = toCharList(rack);
        List<Character> used = toCharList(placement.getRack());
        for (Character c : used) {
            rackLeave.remove(c);
        }
        // RackLeave now in rackLeave as a list with Characters

        // Precompute Hashmap containing heuristic values for characters
        HashMap<Character, Tuple> letterMap = new HashMap<Character, Tuple>();
        letterMap.put('a', new Tuple(0.5, -8.0));
        letterMap.put('b', new Tuple(-3.5, -8.0));
        letterMap.put('c', new Tuple(-0.5, -7.0));
        letterMap.put('d', new Tuple(-1.0, -6.0));
        letterMap.put('e', new Tuple(4.0, -3.5));
        letterMap.put('f', new Tuple(-3.0, -6.0));
        letterMap.put('g', new Tuple(-3.5, -10.0));
        letterMap.put('h', new Tuple(0.5, -6.0));
        letterMap.put('i', new Tuple(-1.5, -10.0));
        letterMap.put('j', new Tuple(-2.5, -2.5));
        letterMap.put('k', new Tuple(-1.5, -1.5));
        letterMap.put('l', new Tuple(-1.5, -6.0));
        letterMap.put('m', new Tuple(-0.5, -6.0));
        letterMap.put('n', new Tuple(0.0, -5.5));
        letterMap.put('o', new Tuple(-2.5, -8.0));
        letterMap.put('p', new Tuple(-1.5, -6.0));
        letterMap.put('q', new Tuple(-11.5, -11.5));
        letterMap.put('r', new Tuple(1.0, -9.0));
        letterMap.put('s', new Tuple(7.5, 1.0));
        letterMap.put('t', new Tuple(-1.0, -6.0));
        letterMap.put('u', new Tuple(-4.5, -12.0));
        letterMap.put('v', new Tuple(-6.5, -8.0));
        letterMap.put('w', new Tuple(-4.0, -8.0));
        letterMap.put('x', new Tuple(3.5, 3.5));
        letterMap.put('y', new Tuple(-2.5, -10.0));
        letterMap.put('z', new Tuple(3.0, 3.0));

        HashMap<Double, HashMap<Double, Double>> balanceMap = new HashMap<Double, HashMap<Double, Double>>();
        HashMap<Double, Double> zeroVowels = new HashMap<Double, Double>();
        HashMap<Double, Double> oneVowel = new HashMap<Double, Double>();
        HashMap<Double, Double> twoVowels = new HashMap<Double, Double>();
        HashMap<Double, Double> threeVowels = new HashMap<Double, Double>();
        HashMap<Double, Double> fourVowels = new HashMap<Double, Double>();
        HashMap<Double, Double> fiveVowels = new HashMap<Double, Double>();
        HashMap<Double, Double> sixVowels = new HashMap<Double, Double>();
        balanceMap.put(0.0, zeroVowels);
        balanceMap.put(1.0, oneVowel);
        balanceMap.put(2.0, twoVowels);
        balanceMap.put(3.0, threeVowels);
        balanceMap.put(4.0, fourVowels);
        balanceMap.put(5.0, fiveVowels);
        balanceMap.put(6.0, sixVowels);

        balanceMap.get(0.0).put(0.0, 0.0);
        balanceMap.get(1.0).put(0.0, -0.5);
        balanceMap.get(2.0).put(0.0, -2.0);
        balanceMap.get(3.0).put(0.0, -3.0);
        balanceMap.get(4.0).put(0.0, -5.0);
        balanceMap.get(5.0).put(0.0, -7.5);
        balanceMap.get(6.0).put(0.0, -12.5);

        balanceMap.get(0.0).put(1.0, 0.5);
        balanceMap.get(1.0).put(1.0, 1.5);
        balanceMap.get(2.0).put(1.0, -0.5);
        balanceMap.get(3.0).put(1.0, -2.0);
        balanceMap.get(4.0).put(1.0, -4.5);
        balanceMap.get(5.0).put(1.0, -7.0);

        balanceMap.get(0.0).put(2.0, 1.5);
        balanceMap.get(1.0).put(2.0, 1.0);
        balanceMap.get(2.0).put(2.0, 0.5);
        balanceMap.get(3.0).put(2.0, -0.5);
        balanceMap.get(4.0).put(2.0, -3.0);

        balanceMap.get(0.0).put(3.0, 0.0);
        balanceMap.get(1.0).put(3.0, 0.5);
        balanceMap.get(2.0).put(3.0, 0.0);
        balanceMap.get(3.0).put(3.0, 1.5);

        balanceMap.get(0.0).put(4.0, -3.5);
        balanceMap.get(1.0).put(4.0, -2.5);
        balanceMap.get(2.0).put(4.0, -2.0);

        balanceMap.get(0.0).put(5.0, -6.0);
        balanceMap.get(1.0).put(5.0, -5.5);

        balanceMap.get(0.0).put(6.0, -9.0);

        List<Character> checked = new ArrayList<Character>();
        for (int i = 0; i < rackLeave.size(); i++) {
            // Update vowel / consonant count
            if (Score.isVowel(rackLeave.get(i))) {
                vowelCount = vowelCount + 1.0;
            } else {
                consCount = consCount + 1.0;
            }
            // Add/subtract value for each letter checking for duplicates
            if (checked.contains(rackLeave.get(i))) {
                heuristic += letterMap.get(rackLeave.get(i)).getLast();
            } else {
                heuristic += letterMap.get(rackLeave.get(i)).getFirst();
            }
            checked.add(rackLeave.get(i));
        }
        heuristic += balanceMap.get(vowelCount).get(consCount);

        // Adjust for special cases i.e. combinations QU, GIN, IVE, OTU

        if (rackLeave.contains('q') && rackLeave.contains('u')) {
            heuristic += 17.0;
        }
        if (rackLeave.contains('g') && rackLeave.contains('i') && rackLeave.contains('n')) {
            heuristic += 6.0;
        }
        if (rackLeave.contains('i') && rackLeave.contains('v') && rackLeave.contains('e')) {
            heuristic += 1.7;
        }
        if (rackLeave.contains('o') && rackLeave.contains('t') && rackLeave.contains('u')) {
            heuristic += 3.0;
        }
        return heuristic;
    }

    public static List<Character> toCharList(String string) {
        List<Character> chars = new ArrayList<Character>();
        if (string == null) {
            return chars;
        }
        for (int i = 0; i < string.length(); i++) {
            chars.add(string.charAt(i));
        }
        return chars;
    }

    public static String removeChar(String string, char c) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == c) {
                return removeCharAt(string, i);
            }
        }
        return null;
    }

    public static String removeCharAt(String string, int index) {
        return string.substring(0,index) + string.substring(index+1,string.length());
    }

    /*

    Heuristics for exchanging letters on pass

     */

    public static String rackExchange(Board board, String rack) {
        HashMap<Character, Tuple> letterMap = new HashMap<Character, Tuple>();
        letterMap.put('a', new Tuple(0.5, -8.0));
        letterMap.put('b', new Tuple(-3.5, -8.0));
        letterMap.put('c', new Tuple(-0.5, -7.0));
        letterMap.put('d', new Tuple(-1.0, -6.0));
        letterMap.put('e', new Tuple(4.0, -3.5));
        letterMap.put('f', new Tuple(-3.0, -6.0));
        letterMap.put('g', new Tuple(-3.5, -10.0));
        letterMap.put('h', new Tuple(0.5, -6.0));
        letterMap.put('i', new Tuple(-1.5, -10.0));
        letterMap.put('j', new Tuple(-2.5, -2.5));
        letterMap.put('k', new Tuple(-1.5, -1.5));
        letterMap.put('l', new Tuple(-1.5, -6.0));
        letterMap.put('m', new Tuple(-0.5, -6.0));
        letterMap.put('n', new Tuple(0.0, -5.5));
        letterMap.put('o', new Tuple(-2.5, -8.0));
        letterMap.put('p', new Tuple(-1.5, -6.0));
        letterMap.put('q', new Tuple(-11.5, -11.5));
        letterMap.put('r', new Tuple(1.0, -9.0));
        letterMap.put('s', new Tuple(7.5, 1.0));
        letterMap.put('t', new Tuple(-1.0, -6.0));
        letterMap.put('u', new Tuple(-4.5, -12.0));
        letterMap.put('v', new Tuple(-6.5, -8.0));
        letterMap.put('w', new Tuple(-4.0, -8.0));
        letterMap.put('x', new Tuple(3.5, 3.5));
        letterMap.put('y', new Tuple(-2.5, -10.0));
        letterMap.put('z', new Tuple(3.0, 3.0));


        List<String> bitStrings = new ArrayList<String>();
        for (int i = 0; i < Math.pow(2, rack.length()); i++) {
            String string = Integer.toBinaryString(i);
            while (string.length() < rack.length()) {
                string = '0' + string;
            }
            bitStrings.add(string);
        }

        List<RackCandidate> rackCandidates = new ArrayList<RackCandidate>();
        for (String bitString : bitStrings) {
            String string = "";
            for (int i = 0; i < bitString.length(); i++) {
                if (bitString.charAt(i) == '1') {
                    string += rack.charAt(i);
                }
            }
            rackCandidates.add(new RackCandidate(string));
        }
        for (int i = 0; i < rackCandidates.size(); i++) {
            double score = Heuristics.rackScore(rackCandidates.get(i).getRack());
            rackCandidates.get(i).setScore(score);
        }

        // Now subtract average rack score

        double totalBagScore = 0;
        List<Character> leftInBag = Bag.getCharactersLeftInBag(board);
        for (Character c: leftInBag) {
            totalBagScore += letterMap.get(c).getFirst();
        }
        double averageBagScore = totalBagScore / leftInBag.size();

        for (int i = 0; i < rackCandidates.size(); i++) {
            double score = rackCandidates.get(i).getScore();
            score += (7 - rackCandidates.get(i).getRack().length())*averageBagScore;
            rackCandidates.get(i).setScore(score);
        }

        Collections.sort(rackCandidates);

        String keep = rackCandidates.get(0).getRack();
        String thrw = "";
        for (int i = 0; i < rack.length(); i++) {
            if (!keep.contains(Character.toString(rack.charAt(i)))) {
                thrw += rack.charAt(i);
            }
        }
        return thrw;
    }

    public static int fishing(Dictionary dict, Board board, String rack) {
        int value = 0;
        ArrayList<Placement> list = getCandidates(dict, board, rack);
        List<Character> lettersLeft = Bag.getCharactersLeftInBag(board);
        for (Placement placement : list) {
            List<String> candidates = dict.getExtendedWords(placement.getWord(), 7-rack.length());
            for (String candidate : candidates) {
                if (candidate.length() == placement.getWord().length() + 7-rack.length()) {
                    int temp = 1;
                    for(int i = 0; i < 7-rack.length(); i++) {
                        if (!lettersLeft.contains(candidate.charAt(placement.getWord().length() + i))) {
                            temp = 0;
                            break;
                        }
                    }
                    value += temp;
                }
            }
        }
        return value/(7-rack.length());
    }

}