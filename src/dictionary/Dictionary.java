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
    private String[] words;
    private File file = new File("src/dictionary/scrabblewords.txt");
    private Node DAWG;

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
    }

	private boolean containLockedLetter(char[] lockedLetters, int position){
		for(int i = 0; i < position; i++){
			if(lockedLetters[i] != '_'){
				return true;
			}
		}
		return false;
	}

	/**
	 * Created the DAWG that will be easily searchable.
	 */
    private void createDAWG() {
        DAWG = new Node(' ', Type.NOT_YET_A_WORD, null);
	    Node currentNode;
	    Node childNode;
	    Node smiliarChildNode;
        for (int i = 0; i < words.length; i++) {
	        currentNode = DAWG;
            char[] word = words[i].toCharArray();
	        for(int j = 0; j < word.length; j++){
		        // Make the child node if not existing
		        childNode = new Node(word[j], Type.NOT_YET_A_WORD, currentNode);
		        if(j == word.length-1){
			        childNode.setType(Type.END_OF_STRING);
		        }
		        smiliarChildNode = currentNode.contain(childNode);
		    	if (smiliarChildNode == null){
					currentNode.addChild(childNode);
				    currentNode = childNode;
			    } // Edit child node if it exists
		        else {
				    if ((childNode.getType() == Type.NOT_YET_A_WORD && smiliarChildNode.getType() == Type.END_OF_STRING)){
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
	private Type search(String string){
		char[] word = string.toCharArray();
		Node currentNode = DAWG;
		Node tempNode;
		for (int i = 0; i < word.length; i++){
			tempNode = new Node(word[i], Type.NOT_YET_A_WORD, currentNode);
			Node tempComparisonNode = currentNode.contain(tempNode);
			if(tempComparisonNode == null){
				return Type.NOT_POSSIBLY_A_WORD;
			}
			else if(tempComparisonNode.getType() == Type.NOT_YET_A_WORD && i == word.length-1){
				return Type.NOT_YET_A_WORD;
			}
			currentNode = tempComparisonNode;
		}
		return currentNode.getType();
	}

	private List<String> getWords(String subString, List<Character> bag, char[] lockedLetters, int position){
		List<String> list = new ArrayList<String>();
		Type type = search(subString);
		if(type == Type.END_OF_WORD && containLockedLetter(lockedLetters, position) && position == 15 && bag.size() < 7){
			List<String> returnValue = new ArrayList<String>();
			returnValue.add(subString);
			return returnValue;
		}
		if(type == Type.END_OF_WORD && containLockedLetter(lockedLetters, position) && lockedLetters[position] == '_'
				&& bag.size() < 7){
			list.add(subString);
		}
		else if (type == Type.END_OF_STRING){
			if(containLockedLetter(lockedLetters, position) && (position == 15 || lockedLetters[position] ==
					'_') && bag.size() < 7){
				List<String> returnValue = new ArrayList<String>();
				returnValue.add(subString);
				return returnValue;
			}
			return null;
		} else if (bag.size() == 0 || position == 15){
			return null;
		}
		List<String> tempList;
		if(lockedLetters[position] != '_'){
			   tempList = getWords(subString+lockedLetters[position], bag, lockedLetters, position+1);
				if(tempList != null){
					list.addAll(tempList);
				}
		}
		else {
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
				tempList = getWords(subString, tempBag, lockedLetters, position+1);
				if(tempList != null){
					list.addAll(tempList);
				}
				// Revert, and shift character.
				subString = subString.substring(0,subString.length()-1);
				tempBag.add(removed);
			}
		}
		return list;
	}

    public List<String>[] getWords(List<Character> bag, char[] lockedLetters){
	    List<String>[] lists = (ArrayList<String>[])new ArrayList[15];
	    int min = -1;
	    int max = -1;
	    for(int i = 0; i < lockedLetters.length; i++){
		     if(lockedLetters[i] != '_'){
			     if(min == -1){
				     min = i;
			     }
			     max = i;
		     }
	    }
	    if(min == -1){
		    return lists;
	    }
	    for(int i = min; i < max+1; i++){
		    if(i == min || lockedLetters[i-1] == '_'){
			    lists[i] = getWords("", bag, lockedLetters, i);
		    }
	    }
	    return lists;
    }

	public static void main(String[] args) {
		Dictionary dict = new Dictionary();
		char[] row = {'a', 'b', 'b', 'a', '_', '_', '_', '_', '_', '_', '_', '_', '_', '_', '_'};
		List<Character> rack = new ArrayList<Character>();
		rack.add('a');
		rack.add('t');
		rack.add('e');
		rack.add('e');
		rack.add('e');
		rack.add('e');
		rack.add('e');
		List<String>[] list = dict.getWords(rack, row);
		for(int i = 0; i < list.length; i++){
			System.out.println(list[i]);
		}

    }
}
