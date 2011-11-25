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
import java.util.*;

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
	    createDAWG();

	    List<Character> chars = new ArrayList<Character>();
	    chars.add('e');
	    chars.add('h');
	    chars.add('l');
	    chars.add('l');
	    chars.add('o');
	    chars.add('r');
	    chars.add('s');
	    chars.add('t');
	    long time = System.currentTimeMillis();
	    List<String> words = getWords("", chars);
	    time = System.currentTimeMillis()-time;
	    System.out.println("Found: " + words);
	    System.out.println("Number of elements: " + words.size());
	    System.out.println("Time used: " + time + "ms");
    }

	/**
	 * Created the DAWG that will be easily searchable.
	 */
    public void createDAWG() {
        DAWG = new Node(' ', Type.NOT_A_WORD, null);
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
				    if ((childNode.getType() == Type.NOT_A_WORD && smiliarChildNode.getType() == Type.END_OF_STRING)){
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

	public List<String> getWords(String subString, List<Character> bag){
		List<String> list = new ArrayList<String>();
		Type type = search(subString);
		if(type == Type.END_OF_WORD){
			list.add(subString);
		}
		else if (type == Type.END_OF_STRING){
			List<String> returnValue = new ArrayList<String>();
			returnValue.add(subString);
			return returnValue;
		} else if (bag.size() == 0){
			return null;
		}
		List<String> tempList;
		List<Character> tempBag = new ArrayList<Character>(bag.size());
		Set<Character> tempSet = new HashSet<Character>();
		for(Character item: bag) tempBag.add(item);
		for(Character item: bag) tempSet.add(item);
		Iterator<Character> it = tempSet.iterator();
		while(it.hasNext()){
			// Add a new character to the string and remove it from the bag.
		    Character removed = it.next();
			subString += removed;
			tempBag.remove(tempBag.indexOf(removed));
			tempList = getWords(subString, tempBag);
			if(tempList != null){
				list.addAll(tempList);
			}
			// Revert, and shift character.
			subString = subString.substring(0,subString.length()-1);
			tempBag.add(removed);
		}
		return list;
	}

    public static void main(String[] args) {
        Dictionary dict = new Dictionary();
    }
}