package logic;

/**
 * Created by IntelliJ IDEA.
 * User: PalCSNjolstad
 * Date: 06.12.11
 * Time: 09.42
 * To change this template use File | Settings | File Templates.
 */
public class RackCandidate implements Comparable<RackCandidate>{

    public String rack;
    public double score;

    public RackCandidate(String rack, double score) {
        this.rack = rack;
        this.score = score;
    }

    public RackCandidate(String rack) {
        this.rack = rack;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return this.score;
    }

    public int compareTo(RackCandidate other) {
        if (this.getScore() > other.getScore()) {
            return -1;
        } else if (this.getScore() < other.getScore()) {
            return 1;
        } else {
            return 0;
        }
    }



}
