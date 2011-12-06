package logic;

import dictionary.Dictionary;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 05.12.11
 * Time: 20.08
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    public static void main(String[] args) {
        Dictionary dict = new Dictionary();
        List<String> words = dict.getExtendedWords("car", 3);
        for (String word : words) {
            System.out.println(word);
        }
    }
}
