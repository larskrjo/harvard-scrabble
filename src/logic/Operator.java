package logic;

import dictionary.Dictionary;
import dictionary.Direction;
import dictionary.Type;
import gui.GUI;
import statistics.Stats;

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

	boolean Agreedy;
	boolean ApositionEvaluation;
	boolean ArackLeave;
	boolean ArackExchange;

	boolean Bgreedy;
	boolean BpositionEvaluation;
	boolean BrackLeave;
	boolean BrackExchange;

	// Different ways of running
	public static boolean GUI_ON = false;
	public static boolean SHOW_HEURISTIC_INFO = false;

    public String makeMove() {
	    String new_word = "--";
        String rack;
        if (turn == Turn.PLAYER_A) {
            rack = this.playerA.getLetters().toString();
	        greedy = Agreedy;
	        positionEvaluation = ApositionEvaluation;
	        rackLeave = ArackLeave;
	        rackExchange = ArackExchange;

        } else {
            rack = this.playerB.getLetters().toString();
	        greedy = Bgreedy;
	        positionEvaluation = BpositionEvaluation;
	        rackLeave = BrackLeave;
	        rackExchange = BrackExchange;
        }
        Placement placement;
        if (board.isEmpty()) {
            placement = Intelligence.getFirstPlacement(this.dictionary, this.board, rack, false);
        } else {
            placement = Intelligence.getPlacement(this.dictionary, this.board, rack, positionEvaluation ,rackLeave,
		            greedy);
        }

        if(placement == null) {
	        String rack_change = rack;
	        if(rackExchange){
		        rack_change = Intelligence.rackExchange(this.board, rack);
	        }
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
        while(!endGame()) {
            makeMove();
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
        while(!endGame()) {
            makeMove();
        }
		if(GUI_ON)
	        gui.finished();
	}

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
	    final Stats statsForA = new Stats();
	    final Stats statsForB = new Stats();
	    List<Thread> threads = new ArrayList<Thread>();
	    for(int i = 0; i < 10; i++){
		    Thread t = new Thread(new Runnable() {
			    public void run() {
				    Operator operator = new Operator();
				    // Set heuristics for Knut
					operator.Agreedy = false;
					operator.ApositionEvaluation = false;
					operator.ArackLeave = false;
					operator.ArackExchange = false;

		            // Set heuristics for Ola
					operator.Bgreedy = true;
					operator.BpositionEvaluation = false;
					operator.BrackLeave = false;
					operator.BrackExchange = false;

	                operator.newGame();
				    statsForA.updateScore(operator.playerA.score);
		            statsForB.updateScore(operator.playerB.score);
			    }
		    });
		    t.start();
		    threads.add(t);
	    }
	    for(Thread t: threads){
		    while(t.isAlive()){
		    }
	    }
	    int averageAScore = statsForA.getAvgScore();
	    int averageBScore = statsForB.getAvgScore();
	    System.out.println("Average score for old player: " + averageAScore);
	    System.out.println("Average score for new player: " + averageBScore);
    }
}
