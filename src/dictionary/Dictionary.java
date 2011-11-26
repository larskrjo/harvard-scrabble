package dictionary; /**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 16.11.11
 * Time: 20.11
 * To change this template use File | Settings | File Templates.
 */
import logic.Board;
import logic.Field;
import logic.Placement;

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

	private boolean containLockedLetter(Field[] lockedLetters, int position){
		for(int i = 0; i < position; i++){
			if(lockedLetters[i].getLetter() != ' '){
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

	private List<String> getWords(String subString, List<Character> bag, Field[][] field, int position,
	                              int sizeOfBag, int index, Direction direction, char current_letter){
		Field[] lockedLetters = new Field[15];
		if(direction == Direction.HORIZONTAL){
			lockedLetters = field[index];
		}
		else {
			for(int i = 0; i < field.length; i++){
			    lockedLetters[i] = field[i][index];
		    }
		}
		if(current_letter != ' ' && subString.length() > 0 && !validInOppositeDirection(field, position-1,index,
				direction, subString.charAt(subString.length()-1))){
			return null;
		}
		List<String> list = new ArrayList<String>();
		Type type = search(subString);
		if(type == Type.END_OF_WORD && containLockedLetter(lockedLetters, position) && position == 15 && bag.size() < sizeOfBag){
			List<String> returnValue = new ArrayList<String>();
			returnValue.add(subString);
			return returnValue;
		}
		if(type == Type.END_OF_WORD && containLockedLetter(lockedLetters, position) && lockedLetters[position].getLetter() ==
				' ' && bag.size() < sizeOfBag){
			list.add(subString);
		}
		else if (type == Type.END_OF_STRING){
			if(containLockedLetter(lockedLetters, position) && (position == 15 || lockedLetters[position].getLetter() ==
					' ') && bag.size() < sizeOfBag){
				List<String> returnValue = new ArrayList<String>();
				returnValue.add(subString);
				return returnValue;
			}
			return null;
		}
		else if (bag.size() == 0 || position == 15){
			return null;
		}
		List<String> tempList;
		// Force to search for the predetermined letter
		if(lockedLetters[position].getLetter() != ' '){
			tempList = getWords(subString+lockedLetters[position].getLetter(), bag, field, position+1, sizeOfBag,
					index, direction, ' ');
			if(tempList != null){
				list.addAll(tempList);
			}
		} // Else search for one letter in the bag
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
				tempList = getWords(subString, tempBag, field, position+1, sizeOfBag, index, direction, removed);
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

	private boolean validInOppositeDirection(Field[][] field, int x, int y, Direction direction, char current_letter) {
		String string = "";
		int beginIndex;
		int endIndex;
		if(direction == Direction.HORIZONTAL){
			beginIndex = Math.max(y-1, 0);
			endIndex = Math.min(y+1, 14);
			for(int i = beginIndex; i >= 0; i--){
				if(field[i][x].getLetter() == ' '){
					beginIndex = i+1;
					break;
				}
				if(i == 0){
					beginIndex = 0;
				}
			}
			for(int i = endIndex; i <= 14; i++){
				if(field[i][x].getLetter() == ' '){
					endIndex = i-1;
					break;
				}
				if(i == 14){
					endIndex = 14;
				}
			}
			string = "";
			for(int i = beginIndex; i <= endIndex; i++){
				if(i == y){
					string += current_letter;
				}
				else{
					string += field[i][x].getLetter();
				}
			}
		}
		else {
			beginIndex = Math.max(x-1, 0);
			endIndex = Math.min(x+1, 14);
			for(int i = beginIndex; i >= 0; i--){
				if(field[y][i].getLetter() == ' '){
					beginIndex = i+1;
					break;
				}
				if(i == 0){
					beginIndex = 0;
				}
			}
			for(int i = endIndex; i <= 14; i++){
				if(field[y][i].getLetter() == ' '){
					endIndex = i-1;
					break;
				}
				if(i == 14){
					endIndex = 14;
				}
			}
			string = "";
			for(int i = beginIndex; i <= endIndex; i++){
				if(i == x){
					string += current_letter;
				}
				else{
					string += field[y][i].getLetter();
				}
			}
		}
		if(string.length() > 1){
			Type type = search(string);
			if(type == Type.NOT_POSSIBLY_A_WORD || type == Type.NOT_YET_A_WORD){
				return false;
			}
		}
		return true;
	}

	public List<String>[] getWords(List<Character> bag, Field[][] field, int index, Direction direction){
	    int min = -1;
	    int max = -1;
		Field[] lockedLetters = new Field[15];
		/*
		Calculate the correct row or column to inspect
		 */
	    if(direction == Direction.HORIZONTAL){
			lockedLetters = field[index];
	    }
	    else{
		    for(int i = 0; i < field.length; i++){
			    lockedLetters[i] = field[i][index];
		    }
	    }
		/*
		 Make sure we don't search for unnecessary indices
		  */
		for(int i = 0; i < field.length; i++){
			if(lockedLetters[i].getLetter() != ' '){
				if(min == -1){
					min = i;
				}
		        max = i;
		    }
		}
		List<String>[] lists = (ArrayList<String>[])new ArrayList[15];
		if(min == -1){
			return lists;
		}
		min = Math.max(0, min-bag.size());
		/*
		 Search for the relevant indices
		  */
		for(int i = min; i < max+1; i++){
			if(i == min || lockedLetters[i-1].getLetter() == ' '){
				lists[i] = getWords("", bag, field, i, bag.size(), index, direction, ' ');
			}
		}
	    return lists;
    }

	public static void main(String[] args) {
		Dictionary dict = new Dictionary();
		Board board = new Board();
		board.addWord(new Placement("taae", 2,0,true));
		board.addWord(new Placement("test", 3,0,true));
		board.addWord(new Placement("ape", 4,3,false));
		board.addWord(new Placement("loses", 2,4,false));

		for(int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				System.out.print(board.getGrid()[i][j].getLetter());
			}
			System.out.println();
		}
		List<Character> rack = new ArrayList<Character>();
		rack.add('a');
		rack.add('t');
		rack.add('e');
		rack.add('s');
		rack.add('k');
		rack.add('l');
		List<String>[] list1 = dict.getWords(rack, board.getGrid(), 4, Direction.HORIZONTAL);
		List<String>[] list2 = dict.getWords(rack, board.getGrid(), 2, Direction.VERTICAL);
		System.out.println("--------------------HORIZONTAL-----------------");
		System.out.println("row: " + 4);
		for(int i = 0; i < list1.length; i++){
			System.out.println(list1[i]);
		}
		System.out.println("--------------------VERTICAL-----------------");
		System.out.println("col: " + 2);
		for(int i = 0; i < list2.length; i++){
			System.out.println(list2[i]);
		}

    }
}
