package logic;

import dictionary.Dictionary;
import gui.GUI;

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
    //playerA's turn implies turn = true
    public boolean turn;

    public Operator() {
        this.board = new Board();
        this.dictionary = new Dictionary();
        this.bag = new Bag();
        this.playerA = new Player(this.bag.drawPlayerStacks());
        this.playerB = new Player(this.bag.drawPlayerStacks());
        this.gui = new GUI(this);
    }

    public void makeMove() {
        String rack;
        if (this.turn) {
            rack = this.playerA.getLetters().toString();
        } else {
            rack = this.playerB.getLetters().toString();
        }
        System.out.println("Rack: " + rack);
        Placement placement = Intelligence.getPlacement(this.dictionary, this.board, rack);

        this.board.addWord(placement);
        int score = this.board.computeScore(placement);

        if (this.turn) {
            this.playerA.addScore(score);
            String usedRack = placement.getRack();
            System.out.println("Rack " + usedRack + ", word " + placement.getWord());
            this.playerA.removeWord(usedRack);
            while(!this.playerA.isRackFull()) {
                 this.playerA.addLetter(this.bag.drawLetter());
            }
        } else {
            this.playerB.addScore(score);
            String usedRack = placement.getRack();
            System.out.println("Rack " + usedRack + ", word " + placement.getWord());
            this.playerB.removeWord(usedRack);
            while(!this.playerB.isRackFull()) {
                 this.playerB.addLetter(this.bag.drawLetter());
            }
        }
        changeTurn();
        this.gui.update();
    }

    public boolean endGame() {
        return this.bag.isEmpty();
    }

    public boolean isTurn() {
        return this.turn;
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
            return "logic.Player A";
        } else if (winner() == this.playerB) {
            return "logic.Player B";
        } else {
            return "Draw";
        }
    }

    public void setTurn(boolean A) {
        this.turn = A;
    }

    public void changeTurn() {
        this.turn = !this.turn;
    }

    public static void main(String[] args) {
        Operator operator = new Operator();
        Placement placement = new Placement("test", 4, 4, true);
        operator.board.addWord(placement);
        System.out.println("Spillet er opprettet");
        while(!operator.endGame()) {
            System.out.println("Inne i while");
            operator.makeMove();
            System.out.println("Har gjort moves");
        }
        System.out.println("The winner is: " + operator.winnerToString() + " with a total score: " + operator.winner().getScore());
    }
}