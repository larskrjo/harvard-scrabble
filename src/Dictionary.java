/**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 16.11.11
 * Time: 20.11
 * To change this template use File | Settings | File Templates.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.ListResourceBundle;

public class Dictionary {
    public List<String> words;
    public File file = new File("dictionary/scrabblewords.txt");
    public static final int NumberOfLetters = 26;
    public static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    public Node dawg;

    public Dictionary() {
        this.words = new ArrayList<String>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                words.add(str);
            }
            in.close();
        } catch (IOException ex) {
            System.out.println("Something went wrong reading the file:");
            System.out.println(ex);
        }
    }

    public List<String> getList() {
        return this.words;
    }

    public void createDawg() {
        this.dawg = new Node("root", null);
        List<Node> nodes = new ArrayList<Node>();

        for (int i = 0; 0 < alphabet.length(); i++) {
            String letter = alphabet.substring(0, 1);
            Node node = new Node(letter, this.dawg);
            nodes.add(node);
        }
        for (String word : this.words) {
            String letter = word.substring(0,1);
            Node parent = nodes.get(nodes.indexOf(letter));
            parent.addChild(word.substring(1));
        }
    }

    public static void main(String[] args) {
        Dictionary dict = new Dictionary();
        List<String> temp = dict.getList();
        for (int i = 0; i < temp.size(); i++) {
            System.out.println("Word is:" + temp.get(i));
        }
    }
}
