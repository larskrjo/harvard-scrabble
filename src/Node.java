import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 16.11.11
 * Time: 22.14
 * To change this template use File | Settings | File Templates.
 */
public class Node {
    public ArrayList<Node> children;
    public Node parent;
    public String value;

    // EndOfWord and EndOfString are flags; 0 means not and 1 means that they are
    public int EndOfWord = 0;
    public int EndOfString = 0;

    public Node(String value, Node parent) {
        this.value = value;
        if (parent == null && value.equals("root")) {
            this.parent = null;
        } else {
            this.parent = parent;
        }
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public void addChild(String child) {
        Node temp = new Node(child, this);
        this.children.add(temp);
    }

    public String getValue() {
        return value;
    }

    public int getEndOfWord() {
        return EndOfWord;
    }

    public int getEndOfString() {
        return EndOfString;
    }

    public void setEndOfWord(int endOfWord) {
        EndOfWord = endOfWord;
    }

    public void setEndOfString(int endOfString) {
        EndOfString = endOfString;
    }
}
