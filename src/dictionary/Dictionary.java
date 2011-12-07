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
    private File file = new File("dictionary/words.txt");
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

	private boolean containLockedLetter(Field[] lockedLetters,int start_index, int position){
		for(int i = start_index; i <= position; i++){
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
	public Type search(String string){
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

	private List<String> getWords(String subString, List<Character> bag, Field[][] field, int next_position,
	                              int fixed_position, boolean connected, Direction direction, char current_letter,
	                              int start_index){
		Field[] lockedLetters = new Field[15];
		if(direction == Direction.HORIZONTAL){
			lockedLetters = field[fixed_position];
			if(current_letter != ' ' && subString.length() > 0 && !validInOppositeDirection(field,
					next_position-1,fixed_position, direction, subString.charAt(subString.length()-1), connected)){
				return null;
			}
		}
		else {
			for(int i = 0; i < field.length; i++){
			    lockedLetters[i] = field[i][fixed_position];
		    }
			if(current_letter != ' ' && subString.length() > 0 && !validInOppositeDirection(field, fixed_position,
					next_position-1, direction, subString.charAt(subString.length()-1), connected)){
				return null;
			}
		}

		List<String> list = new ArrayList<String>();
		Type type = search(subString);
		// If finished search
		if(next_position == 15){
			// return if it's a word, contains fixed letters and you have used letters from bag
			if((type == Type.END_OF_WORD || type == Type.END_OF_STRING) &&
				containLockedLetter(lockedLetters,start_index,next_position-1) && connected){
				List<String> returnValue = new ArrayList<String>();
				returnValue.add(subString);
				return returnValue;
			}
			// Else return null
			return null;
		}
		// Else if not finished search
		else if(next_position < 15){
			// add word if it's a word, contains fixed letters, no fixed letter in next position and you have used
			// from bag
			if((type == Type.END_OF_WORD || type == Type.END_OF_STRING) &&
				containLockedLetter(lockedLetters,start_index,next_position-1) && lockedLetters[next_position]
					.getLetter() == ' '
					&& connected){
				list.add(subString);
			}
			// Else search more
		}
		List<String> tempList;
		// If next position is locked, force to search for the predetermined letter
		if(lockedLetters[next_position].getLetter() != ' '){
			next_position += 1;
			tempList = getWords(subString+lockedLetters[next_position-1].getLetter(), bag, field, next_position,
					fixed_position, connected, direction, ' ',start_index);
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
			next_position += 1;
			while(it.hasNext()){
				Character removed = it.next();

				// Add a new character to the string and remove it from the bag.
				subString += removed;
				tempBag.remove(tempBag.indexOf(removed));
				tempList = getWords(subString, tempBag, field, next_position, fixed_position, true, direction,
						removed,start_index);
				// Reverse operation
				tempBag.add(removed);
				subString = subString.substring(0,subString.length()-1);

				if(tempList != null){
					list.addAll(tempList);
				}
			}
		}
		return list;
	}

	private boolean validInOppositeDirection(Field[][] field, int x, int y, Direction direction, char current_letter,
	 boolean connected) {
		String string = "";
		int beginIndex;
		int endIndex;
		if(direction == Direction.HORIZONTAL){
			beginIndex = Math.max(y-1, 0);
			endIndex = Math.min(y+1, 14);
			for(int i = beginIndex; i >= 0; i--){
				if(y == 0){
					break;
				}
				if(field[i][x].getLetter() == ' '){
					beginIndex = i+1;
					break;
				}
				if(i == 0){
					beginIndex = 0;
				}
			}
			for(int i = endIndex; i <= 14; i++){
				if(y == 14){
					break;
				}
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
				if(x == 0){
					break;
				}
				if(field[y][i].getLetter() == ' '){
					beginIndex = i+1;
					break;
				}
				if(i == 0){
					beginIndex = 0;
				}
			}
			for(int i = endIndex; i <= 14; i++){
				if(x == 14){
					break;
				}
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
			if(!connected){
				connected = true;
			}
		}
		return true;
	}

	public List<String>[] getWords(List<Character> bag, Field[][] field, int index, Direction direction){
	    int min = -1;
	    int max = -1;
		Field[] overLetters = new Field[15];
		Field[] lockedLetters = new Field[15];
		Field[] underLetters = new Field[15];
		/*
		Calculate the correct row or column to inspect
		 */
	    if(direction == Direction.HORIZONTAL){
		    if(index-1 > 0)
			     underLetters = field[index-1];
		    if(index+1 < 14)
			     overLetters = field[index+1];
			lockedLetters = field[index];
	    }
	    else{
		    for(int i = 0; i < field.length; i++){
			    if(index-1 > 0)
			        underLetters[i] = field[i][index-1];
		        if(index+1 < 14)
			        overLetters[i] = field[i][index+1];
				lockedLetters[i] = field[i][index];
		    }
	    }
		/*
		 Make sure we don't search for unnecessary indices
		  */
		for(int i = 0; i < field.length; i++){
			if(underLetters[i] != null && underLetters[i].getLetter() != ' '){
				if(min == -1){
					min = i;
				}
		        max = i;
			}
			if(overLetters[i] != null && overLetters[i].getLetter() != ' '){
				if(min == -1){
					min = i;
				}
		        max = i;
			}
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
		for(int i = min; i <= max; i++){
			if(i == min || lockedLetters[i-1].getLetter() == ' '){
				lists[i] = getWords("", bag, field, i, index, false, direction, ' ', i);
			}
		}
	    return lists;
    }

	public static void main(String[] args) {
		Dictionary dict = new Dictionary();
		Board board = new Board();
		board.addWord(new Placement("stub", 0,2,Direction.VERTICAL));
		for(int i = 0; i < 15; i++){
			for (int j = 0; j < 15; j++){
				System.out.print(board.getGrid()[i][j].getLetter());
			}
			System.out.println();
		}
		List<Character> rack = new ArrayList<Character>();
		rack.add('q');
		rack.add('p');
		rack.add('e');
		rack.add('t');
		rack.add('w');
		rack.add('s');
		rack.add('a');
		List<String>[] list = dict.getWords(rack, board.getGrid(), 8, Direction.VERTICAL);
		System.out.println("--------------------VERTICAL-----------------");
		System.out.println("col: " + 0);
		for(int i = 0; i < list.length; i++){
			System.out.println(list[i]);
		}

    }

    public List<String> getExtendedWords(String string, int len) {
        char[] word = string.toCharArray();
        Node currentNode = DAWG;
        Node tempNode;
        List<String> words = new ArrayList<String>();
        for (int i = 0; i < word.length; i++) {
            tempNode = new Node(word[i], Type.NOT_YET_A_WORD, currentNode);
            Node tempComparisonNode = currentNode.contain(tempNode);
            if(tempComparisonNode == null){
                break;
            }
            currentNode = tempComparisonNode;
            if (i == word.length-1) {
                List<Node> candList = new ArrayList<Node>();
                if(!currentNode.getChildren().isEmpty()) {
                    for(Node child : currentNode.getChildren()) {
                        if (!child.getChildren().isEmpty()) {
                            candList.add(child);
                        }
                        if (child.getType() == Type.END_OF_WORD) {
                            words.add(string + child.getValue());
                        }
                    }
                    for (int j = 0; j < len; j++) {
                        if(!candList.isEmpty()) {
                            List<Node> newList = new ArrayList<Node>();
                            for(Node candidate : candList) {
                                for (Node child : candidate.getChildren()) {
                                    if (child.getType() == Type.END_OF_WORD) {
                                        String temp = "" + child.getValue();
                                        Node temp1 = child;
                                        Node temp2;
                                        for(int k = 0; k < j+1; k++) {
                                            temp2 = temp1.getParent();
                                            temp = temp2.getValue() + temp;
                                            temp1 = temp2;
                                        }
                                        words.add(string + temp);
                                    }
                                    if (j < len-2) {
                                        newList.add(child);
                                    }
                                }
                            }
                            candList = newList;
                        }
                    }
                }
            }
        }
        return words;
    }
}
