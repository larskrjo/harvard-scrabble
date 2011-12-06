package logic;

import dictionary.Dictionary;
import dictionary.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: PalCSNjolstad
 * Date: 05.12.11
 * Time: 15.00
 * To change this template use File | Settings | File Templates.
 */
public class Heuristics {

    public static String rackExchange(Board board, String rack) {

        List<String> bitStrings = new ArrayList<String>();
        for (int i = 0; i < Math.pow(2, rack.length()); i++) {
            String string = Integer.toBinaryString(i);
            while (string.length() < 7) {
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
            double score = rackScore(rackCandidates.get(i).getRack());
            rackCandidates.get(i).setScore(score);
        }

        for (int i = 0; i < rackCandidates.size(); i++) {
            System.out.println(rackCandidates.get(i).getRack() + ", " + rackCandidates.get(i).getScore());
        }
        return null;
    }

    public static double rackScore(String rack) {
        List<Character> rackLeave = toCharList(rack);
        double heuristic = 0;
        double vowelCount = 0;
        double consCount = 0;

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
        System.out.println(vowelCount);
        System.out.println(consCount);
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

        public static void main(String[] args) {

        String rack = "quoteor";
        Board board = new Board();
        Placement placement = new Placement("rut", "r", 0, 0, Direction.HORIZONTAL);
        System.out.println(rackEval(board, rack, placement));

        rackExchange(board, "abcdefg");
        System.out.println(Integer.toBinaryString(127));


    }
}