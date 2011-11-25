import dictionary.Dictionary;

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
        this.gui = new GUI();
    }

    public void makeMove() {
        String rack;
        if (this.turn) {
            rack = this.playerA.getLetters().toString();
        } else {
            rack = this.playerB.getLetters().toString();
        }
        Placement placement = Intelligence.getPlacement(this.dictionary, this.board, rack);
       // this.board.addWord(placement);
        if (this.turn) {
            ;
        } else {
            rack = this.playerB.getLetters().toString();
        }
        this.gui.update();
        //Do changes on Board and GUI
        //Update player (remove and add letters)
        changeTurn();
    }

    public boolean isTurn() {
        return this.turn;
    }

    public void setTurn(boolean A) {
        this.turn = A;
    }

    public void changeTurn() {
        this.turn = !this.turn;
    }
}
