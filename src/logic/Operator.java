package logic;

import dictionary.Dictionary;
import dictionary.Direction;
import dictionary.Type;
import gui.GUI;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 16.11.11
 * Time: 20.11
 * To change this template use File | Settings | File Templates.
 */
public class Operator {
    public Board board;
    public Dictionary dictionary;
    public Player playerA;
    public Player playerB;
    public Bag bag;
    public GUI gui;
    public Turn turn;
	// Heuristics
	boolean greedy;
	boolean positionEvaluation;
    boolean rackLeave;
	boolean rackExchange;

	// Different ways of running
	public static boolean GUI_ON = true;
	public static boolean SHOW_HEURISTIC_INFO = false;

    public String makeMove(List<Boolean> playerAHeuristics, List<Boolean> playerBHeuristics) {
	    String new_word = "--";
        String rack;
        if (turn == Turn.PLAYER_A) {
            rack = this.playerA.getLetters().toString();
	        setPlayerHeuristics(playerAHeuristics);

        } else {
            rack = this.playerB.getLetters().toString();
            setPlayerHeuristics(playerBHeuristics);
        }
        //System.out.println("Rack: " + rack);
        Placement placement;
        if (board.isEmpty()) {
            placement = Intelligence.getFirstPlacement(this.dictionary, this.board, rack, false);
        } else {
            placement = Intelligence.getPlacement(this.dictionary, this.board, rack, positionEvaluation ,rackLeave,
		            greedy);
        }

        if(placement == null) {
            String rack_change = Intelligence.rackExchange(this.board, rack);
            if (turn == Turn.PLAYER_A) {
                for (char letter : rack_change.toCharArray()) {
                    this.playerA.removeLetter(letter);
                    letter = this.bag.exchangeLetter(letter);
                    this.playerA.addLetter(letter);
                }
                this.playerA.newPass();
            } else {
                for (char letter : rack_change.toCharArray()) {
                    this.playerB.removeLetter(letter);
                    letter = this.bag.exchangeLetter(letter);
                    this.playerB.addLetter(letter);
                }
                this.playerB.newPass();
            }
        } else {
	        System.out.println("col: " + placement.getCol() + " row: " + placement.getRow() + " direction: " +
			        placement.getDirection());
            int score = this.board.computeScore(placement);
            if(SHOW_HEURISTIC_INFO)
	            System.out.println("Score: "+ score);
	        new_word = placement.getWord();
            this.board.addWord(placement);
            //System.out.println("The score for this word is: " +  score);
            if (turn == Turn.PLAYER_A) {
                this.playerA.clearPass();
                this.playerA.addScore(score);
                String usedRack = placement.getRack();
	            if(SHOW_HEURISTIC_INFO)
                    System.out.println("Rack " + usedRack + ", word " + placement.getWord());
                this.playerA.removeWord(usedRack);
                while(!this.playerA.isRackFull() && this.bag.letters.size() != 0) {
                     this.playerA.addLetter(this.bag.drawLetter());
                }
            } else {
                this.playerB.clearPass();
                this.playerB.addScore(score);
                String usedRack = placement.getRack();
                //System.out.println("Rack " + usedRack + ", word " + placement.getWord());
                this.playerB.removeWord(usedRack);
                while(!this.playerB.isRackFull() && this.bag.letters.size() != 0) {
                     this.playerB.addLetter(this.bag.drawLetter());
                }
            }
        }
	    if(SHOW_HEURISTIC_INFO)
            System.out.println("Heuristic: " + Intelligence.placementFutureValue(this.dictionary, this.board, placement));
        changeTurn();
	    if(GUI_ON)
            this.gui.update();
	    return new_word;

    }

	public void setPlayerHeuristics(List<Boolean> playerHeuristics){
		greedy = playerHeuristics.get(0);
		positionEvaluation = playerHeuristics.get(1);
		rackLeave = playerHeuristics.get(2);
		rackExchange = playerHeuristics.get(3);
	}

    public boolean endGame() {
        return this.playerA.passLimit() || this.playerB.passLimit();
    }

    public Player winner() {
        if (this.playerA.getScore() > this.playerB.getScore()) {
            return this.playerA;
        } else if(this.playerA.getScore() < this.playerB.getScore()) {
            return this.playerB;
        } else {
            return null;
        }
    }

    public String winnerToString() {
        if (winner() == this.playerA) {
            return "Player A";
        } else if (winner() == this.playerB) {
            return "Player B";
        } else {
            return "Draw";
        }
    }

    public void changeTurn() {
	    if(turn == Turn.PLAYER_A){
			turn = Turn.PLAYER_B;
	    }
	    else {
		    turn = Turn.PLAYER_A;
	    }
	    if(GUI_ON)
	        gui.update();
    }

	public void newGame(){
		System.out.println("New game!");
		this.board = new Board();
        this.dictionary = new Dictionary();
        this.bag = new Bag();
        this.playerA = new Player(this.bag.drawPlayerStacks());
        this.playerB = new Player(this.bag.drawPlayerStacks());
		if(GUI_ON)
            this.gui = new GUI(this);
        if (Math.random() < 0.5) {
            this.turn = Turn.PLAYER_B;
        } else {
            this.turn = Turn.PLAYER_A;
        }
		if(GUI_ON)
			gui.update();
		List<Boolean> playerAHeuristics = new ArrayList<Boolean>();
		setHeuristicsPlayerA(playerAHeuristics);
		List<Boolean> playerBHeuristics = new ArrayList<Boolean>();
		setHeuristicsPlayerB(playerBHeuristics);
        while(!endGame()) {
            makeMove(playerAHeuristics, playerBHeuristics);
        }
		if(GUI_ON)
	        gui.finished();
	}

	public void restartGame() throws InvocationTargetException, InterruptedException {
		System.out.println("New game!");
		this.board = new Board();
        this.dictionary = new Dictionary();
        this.bag = new Bag();
        this.playerA = new Player(this.bag.drawPlayerStacks());
        this.playerB = new Player(this.bag.drawPlayerStacks());
	    this.turn = Turn.PLAYER_A;
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				if(GUI_ON)
					gui.update();
			}
		});
		List<Boolean> playerAHeuristics = new ArrayList<Boolean>();
		setHeuristicsPlayerA(playerAHeuristics);
		List<Boolean> playerBHeuristics = new ArrayList<Boolean>();
		setHeuristicsPlayerB(playerBHeuristics);

        while(!endGame()) {
            makeMove(playerAHeuristics, playerBHeuristics);
        }
		if(GUI_ON)
	        gui.finished();
	}

	private void setHeuristicsPlayerA(List<Boolean> playerAHeuristics){
		// Greedy
		playerAHeuristics.add(false);
		// PositionEvaluation
		playerAHeuristics.add(false);
		// RackLeave
		playerAHeuristics.add(false);
		// RackExchange
		playerAHeuristics.add(false);
	}
	private void setHeuristicsPlayerB(List<Boolean> playerBHeuristics){
		// Greedy
		playerBHeuristics.add(true);
		// PositionEvaluation
		playerBHeuristics.add(true);
		// RackLeave
		playerBHeuristics.add(false);
		// RackExchange
		playerBHeuristics.add(false);
	}

    public static void main(String[] args){
	    Operator operator = new Operator();
	    operator.newGame();
    }
}
