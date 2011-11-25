/**
 * Created by IntelliJ IDEA.
 * User: PalCSNjolstad
 * Date: 25.11.11
 * Time: 16.24
 * To change this template use File | Settings | File Templates.
 */
public class Candidate {

    public Placement placement;
    public int score;

    public Candidate(Placement placement, int score) {
        this.placement = placement;
        this.score = score;
    }

    public Placement getPlacement() {
        return this.placement;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
