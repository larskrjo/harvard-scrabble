/**
 * Created by IntelliJ IDEA.
 * User: PalCSNjolstad
 * Date: 17.11.11
 * Time: 17.48
 * To change this template use File | Settings | File Templates.
 */
public class Placement {

    // Class for representing a placement on the board
    // Specifically used by the Intelligence class for returning the optimal placement

    private String word;
    private String rack;
    private int row;
    private int col;
    private boolean direction; // True for horizontal and False for vertical

    public Placement(String word, int row, int col, boolean direction) {
        this.word = word;
        this.row = row;
        this.col = col;
        this.direction = direction;
    }

    public Placement(String word, String rack, int row, int col, boolean direction) {
        this.rack = rack;
        this.word = word;
        this.row = row;
        this.col = col;
        this.direction = direction;
    }

    public String getWord() {
        return this.word;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public boolean getDirection() {
        return this.direction;
    }

    public String getRack() {
        return this.rack;
    }
}
