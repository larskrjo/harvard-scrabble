package logic;

import dictionary.Dictionary;
import dictionary.Direction;
import dictionary.Type;
import gui.GUI;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

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
	public boolean guiOn = true;
	public boolean showHeuristicInfo = false;

    public String makeMove() {
	    String new_word = "--";
        String rack;
        boolean future;
        boolean rackEval;
        if (turn == Turn.PLAYER_A) {
            rack = this.playerA.getLetters().toString();
            future = false;
            rackEval = false;

        } else {
            rack = this.playerB.getLetters().toString();
            future = false;
            rackEval = false;
        }
        //System.out.println("Rack: " + rack);
        Placement placement;
        if (board.isEmpty()) {
            placement = Intelligence.getFirstPlacement(this.dictionary, this.board, rack);
        } else {
            placement = Intelligence.getPlacement(this.dictionary, this.board, rack, future,rackEval);
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
            if(showHeuristicInfo)
	            System.out.println("Score: "+ score);
	        new_word = placement.getWord();
            this.board.addWord(placement);
            //System.out.println("The score for this word is: " +  score);
            if (turn == Turn.PLAYER_A) {
                this.playerA.clearPass();
                this.playerA.addScore(score);
                String usedRack = placement.getRack();
	            if(showHeuristicInfo)
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
	    if(showHeuristicInfo)
            System.out.println("Heuristic: " + Intelligence.placementFutureValue(this.dictionary, this.board, placement));
        changeTurn();
	    if(guiOn)
            this.gui.update();
	    return new_word;

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
	    if(guiOn)
	        gui.update();
    }

	public void newGame(){
		System.out.println("New game!");
		this.board = new Board();
        this.dictionary = new Dictionary();
        this.bag = new Bag();
        this.playerA = new Player(this.bag.drawPlayerStacks());
        this.playerB = new Player(this.bag.drawPlayerStacks());
		if(guiOn)
            this.gui = new GUI(this);
        if (Math.random() < 0.5) {
            this.turn = Turn.PLAYER_B;
        } else {
            this.turn = Turn.PLAYER_A;
        }

		Placement placement = new Placement("test", 0, 0, Direction.HORIZONTAL);
        board.addWord(placement);
		if(guiOn)
			gui.update();
        while(!endGame()) {
            makeMove();
        }
		if(guiOn)
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

		Placement placement = new Placement("test", 0, 0, Direction.HORIZONTAL);
        board.addWord(placement);
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				if(guiOn)
					gui.update();
			}
		});
        while(!endGame()) {
            makeMove();
        }
		if(guiOn)
	        gui.finished();
	}

    public static void main(String[] args){
        int A = 0;
        int B = 0;
        int A_avg = 0;
        int B_avg = 0;
        for (int i = 0; i < 50; i++) {
            Operator operator = new Operator();
		    operator.newGame();
            if (operator.winner() == operator.playerA) {
                A += 1;
            } else if (operator.winner() == operator.playerB) {
                B += 1;
            }
            A_avg += operator.playerA.getScore();
            B_avg += operator.playerB.getScore();
        }
        A_avg = A_avg/50;
        B_avg = B_avg/50;
        System.out.println("Knut vant " + A + " ganger med " + A_avg + ", Ola vant " + B + " ganger med score " + B_avg);
    }
}
