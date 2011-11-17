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
    //playerA's turn implies turn = true
    public boolean turn;

    public Operator() {
        this.board = new Board();
        this.dictionary = new Dictionary();
        this.bag = new Bag();
        this.playerA = new Player(bag.drawPlayerStacks());
        this.playerB = new Player(bag.drawPlayerStacks());
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
