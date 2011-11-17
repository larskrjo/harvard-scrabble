package dictionary; /**
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

public class Dictionary {
    public String[] words;
    public File file = new File("src/dictionary/scrabblewords.txt");
    public Node DAWG;

    public Dictionary() {
        words = new String[172823];
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
	        int i = 0;
            while ((str = in.readLine()) != null) {
                words[i++] = str;
            }
            in.close();
        } catch (IOException ex) {
            System.out.println("Something went wrong reading the file:");
            System.out.println(ex);
        }
	    createDawg();
    }

    public String[] getList() {
        return words;
    }


	/**
	 * Created the DAWG that will be easily searchable.
	 */
    public void createDawg() {
        DAWG = new Node(' ', Type.END_OF_STRING, null);
	    Node currentNode;
	    Node childNode;
	    Node smiliarChildNode;
        for (int i = 0; i < words.length; i++) {
	        currentNode = DAWG;
            char[] word = words[i].toCharArray();
	        for(int j = 0; j < word.length; j++){
		        // Make the child node if not existing
		        childNode = new Node(word[j], Type.NOT_A_WORD, currentNode);
		        if(j == word.length-1){
			        childNode.setType(Type.END_OF_STRING);
		        }
		        smiliarChildNode = currentNode.contain(childNode);

		    	if (smiliarChildNode == null){
					currentNode.addChild(childNode);
				    currentNode = childNode;
			    } // Edit child node if it exists
		        else {
				    if ((childNode.getType() == Type.NOT_A_WORD && smiliarChildNode.getType() == Type.END_OF_STRING) ||
						    smiliarChildNode.getType() == Type.NOT_A_WORD){
						smiliarChildNode.setType(Type.END_OF_WORD);
				    }
				    currentNode = smiliarChildNode;
			    }
	        }
        }
    }

	/**
	 * This method searches through the DAWG to find either a match for a word or not.
	 * @param string Word that is searched for.
	 * @return The type of the last letter in the word, either END_OF_STRING or END_OF_WORD if the string was found,
	 * or NOT_A_WORD if it was not found.
	 */
	public Type search(String string){
		char[] word = string.toCharArray();
		Node currentNode = DAWG;
		Node tempNode;
		for (int i = 0; i < word.length; i++){
			tempNode = new Node(word[i], Type.NOT_A_WORD, currentNode);
			Node tempComparisonNode = currentNode.contain(tempNode);
			if(tempComparisonNode == null || (tempComparisonNode.getType() == Type.NOT_A_WORD && i == word.length-1)){
				return Type.NOT_A_WORD;
			}
			currentNode = tempComparisonNode;
		}
		return currentNode.getType();
	}

    public static void main(String[] args) {
        Dictionary dict = new Dictionary();
	    Type type = dict.search("aah");
	    System.out.println(type);
    }
}
