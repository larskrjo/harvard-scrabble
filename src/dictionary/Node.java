package dictionary;

import java.util.LinkedList;
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
    private List<Node> children;
    private Node parent;
    private char value;
	private Type type;

    public Node(char value, Type type, Node parent) {
	    children = new ArrayList<Node>();
        this.value = value;
        this.parent = parent;
	    this.type = type;
    }

	// Check whether a node that wants to add to the children list should modify the list or make a new entry.
	// Returns the relevant node if it was found.
	public Node contain(Node node){
		for(int i = 0; i < children.size(); i++){
			if(children.get(i).getValue() == node.value){
				return children.get(i);
			}
		}
		return null;
	}

	public void addChild(Node child) {
        children.add(child);
    }

	public void setType(Type type){
		this.type = type;
	}

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public char getValue() {
        return value;
    }

	public Type getType(){
		return type;
	}
}
